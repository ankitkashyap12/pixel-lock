package com.pixel.applock.security

import android.content.Context
import android.content.SharedPreferences

object SecurityManager {
    private const val PREFS_NAME = "security_prefs"
    private const val PIN_KEY = "pin_set"

    fun isSecuritySetup(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.contains(PIN_KEY)
    }

    fun setPin(context: Context, pin: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit().putString(PIN_KEY, pin).apply()
    }

    fun getPin(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(PIN_KEY, null)
    }
}
