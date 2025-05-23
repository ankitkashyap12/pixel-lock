package com.pixel.applock.data

import android.content.Context
import android.content.pm.PackageManager


class AppRepository(private val context: Context) {
    fun getInstalledApps(): List<AppInfo> {
        val pm = context.packageManager
        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .map {
                AppInfo(
                    pm.getApplicationLabel(it).toString(),
                    it.packageName,
                    pm.getApplicationIcon(it)
                )
            }
    }
}