package com.pixel.applock

import android.content.Context

object AppLockPrefs {
    private const val PREFS_NAME = "AppLockPrefs"
    private const val LOCKED_APPS_KEY = "LockedApps"
    private const val PIN_KEY = "Pin"

    fun getLockedApps(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(LOCKED_APPS_KEY, emptySet()) ?: emptySet()
    }

    fun setLockedApps(context: Context, apps: Set<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putStringSet(LOCKED_APPS_KEY, apps).apply()
    }

    fun addLockedApp(context: Context, packageName: String) {
        val lockedApps = getLockedApps(context).toMutableSet()
        lockedApps.add(packageName)
        setLockedApps(context, lockedApps)
    }

    fun removeLockedApp(context: Context, packageName: String) {
        val lockedApps = getLockedApps(context).toMutableSet()
        lockedApps.remove(packageName)
        setLockedApps(context, lockedApps)
    }

    fun isAppLocked(context: Context, packageName: String): Boolean {
        return getLockedApps(context).contains(packageName)
    }

    // PIN save/load helpers
    fun savePin(context: Context, pin: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(PIN_KEY, pin).apply()
    }

    fun getPin(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(PIN_KEY, null)
    }
}
