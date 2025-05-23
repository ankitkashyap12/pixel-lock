package com.pixel.applock.service

import android.app.*
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pixel.applock.AppLockPrefs
import com.pixel.applock.AuthenticationActivity
import com.pixel.applock.R

class AppMonitorService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var usageStatsManager: UsageStatsManager
    private var lastCheckedPackage = ""
    private var lastCheckTime = 0L
    private val checkInterval = 1000L // Check every 1 second

    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "app_lock_monitor"
        private const val TAG = "AppMonitorService"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")

        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        // Start monitoring
        handler.post(checkForegroundApp)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "Service started")
        return START_STICKY // Restart if killed
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "App Lock Monitor",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Monitors apps for locking"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("App Lock Active")
            .setContentText("Monitoring locked apps")
//            .setSmallIcon(R.drawable.ic_lock) // Make sure you have this icon
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private val checkForegroundApp = object : Runnable {
        override fun run() {
            try {
                val currentPackage = getCurrentForegroundApp()
                val currentTime = System.currentTimeMillis()

                Log.d(TAG, "Current foreground app: $currentPackage")

                if (currentPackage != null &&
                    currentPackage != lastCheckedPackage &&
                    currentTime - lastCheckTime > 500) { // Prevent rapid triggers

                    if (isAppLocked(currentPackage) && !isSystemPackage(currentPackage)) {
                        Log.d(TAG, "Locked app detected: $currentPackage")
                        showAuthenticationScreen(currentPackage)
                    }

                    lastCheckedPackage = currentPackage
                    lastCheckTime = currentTime
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error checking foreground app", e)
            }

            // Schedule next check
            handler.postDelayed(this, checkInterval)
        }
    }

    private fun getCurrentForegroundApp(): String? {
        return try {
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 10000 // Last 10 seconds

            // Use UsageEvents for more accurate detection
            val usageEvents = usageStatsManager.queryEvents(beginTime, endTime)
            var lastResumedApp: String? = null

            while (usageEvents.hasNextEvent()) {
                val event = UsageEvents.Event()
                usageEvents.getNextEvent(event)

                if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                    lastResumedApp = event.packageName
                }
            }

            // Fallback to usage stats method
            if (lastResumedApp == null) {
                val usageStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY, beginTime, endTime
                )
                lastResumedApp = usageStats.maxByOrNull { it.lastTimeUsed }?.packageName
            }

            lastResumedApp
        } catch (e: Exception) {
            Log.e(TAG, "Error getting foreground app", e)
            null
        }
    }

    private fun isAppLocked(packageName: String): Boolean {
        return try {
            AppLockPrefs.isAppLocked(applicationContext, packageName)
        } catch (e: Exception) {
            Log.e(TAG, "Error checking if app is locked: $packageName", e)
            false
        }
    }

    private fun isSystemPackage(packageName: String): Boolean {
        // Don't lock system apps and our own app
        val systemPackages = setOf(
            "com.android.systemui",
            "android",
            "com.android.launcher",
            "com.android.launcher3",
            packageName, // Our own package
            "com.pixel.applock" // Our app package
        )

        return systemPackages.any { packageName.contains(it) }
    }

    private fun showAuthenticationScreen(packageName: String) {
        try {
            val intent = Intent(this, AuthenticationActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("packageToUnlock", packageName)
                putExtra("timestamp", System.currentTimeMillis())
            }

            Log.d(TAG, "Starting authentication for: $packageName")
            startActivity(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error starting authentication activity", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service destroyed")
        handler.removeCallbacks(checkForegroundApp)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}