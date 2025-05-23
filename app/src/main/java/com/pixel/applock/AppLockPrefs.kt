// File: AppLockPrefs.kt

package com.pixel.applock

import android.content.Context
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object AppLockPrefs {

    private const val LOCKED_APPS_KEY = "locked_apps"

    // Save locked apps to SharedPreferences
    suspend fun saveLockedApps(context: Context, apps: Set<String>) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = prefs.edit()
        editor.putStringSet(LOCKED_APPS_KEY, apps)
        editor.apply()
    }

    // Get the set of locked apps from SharedPreferences
    fun getLockedApps(context: Context): Flow<Set<String>> = flow {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val lockedApps = prefs.getStringSet(LOCKED_APPS_KEY, emptySet()) ?: emptySet()
        emit(lockedApps)
    }
}
