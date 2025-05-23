package com.pixel.applock

import android.content.Context

object AppLockPrefs {
    private const val PREFS_NAME = "app_lock_prefs"
    private const val KEY_PIN = "user_pin"
    private const val KEY_LOCKED_APPS = "locked_apps"

    fun savePin(context: Context, pin: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PIN, pin).apply()
    }

    fun getPin(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PIN, null)
    }

    fun saveLockedApps(context: Context, lockedApps: Set<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(KEY_LOCKED_APPS, lockedApps).apply()
    }

    fun getLockedApps(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_LOCKED_APPS, emptySet()) ?: emptySet()
    }

    fun isAppLocked(context: Context, packageName: String): Boolean {
        return getLockedApps(context).contains(packageName)
    }

    fun lockApp(context: Context, packageName: String) {
        val lockedApps = getLockedApps(context).toMutableSet()
        lockedApps.add(packageName)
        saveLockedApps(context, lockedApps)
    }

    fun unlockApp(context: Context, packageName: String) {
        val lockedApps = getLockedApps(context).toMutableSet()
        lockedApps.remove(packageName)
        saveLockedApps(context, lockedApps)
    }
}