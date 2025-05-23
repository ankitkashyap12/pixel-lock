package com.pixel.applock.service

import com.pixel.applock.AppLockPrefs
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.pixel.applock.AuthenticationActivity

class AppMonitorService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var usageStatsManager: UsageStatsManager

    override fun onCreate() {
        super.onCreate()
        usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        handler.post(checkForegroundApp)
    }

    private val checkForegroundApp = object : Runnable {
        override fun run() {
            val endTime = System.currentTimeMillis()
            val beginTime = endTime - 10000
            val usageStats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, beginTime, endTime
            )
            val recentApp = usageStats.maxByOrNull { it.lastTimeUsed }
            recentApp?.packageName?.let { packageName ->
                if (isAppLocked(packageName)) {
                    val intent = Intent(this@AppMonitorService, AuthenticationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    intent.putExtra("packageToUnlock", packageName)
                    startActivity(intent)
                }
            }
            handler.postDelayed(this, 2000)
        }
    }

    private fun isAppLocked(packageName: String): Boolean {
        return AppLockPrefs.isAppLocked(applicationContext,packageName)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
