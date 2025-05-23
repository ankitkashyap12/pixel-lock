package com.pixel.applock.data

import android.graphics.drawable.Drawable

/**
 * Data class representing an installed app's information.
 */
data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable
)
