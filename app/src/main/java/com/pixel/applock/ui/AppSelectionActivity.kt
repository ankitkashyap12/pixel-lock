package com.pixel.applock.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.pixel.applock.AppLockPrefs
import com.pixel.applock.data.AppInfo
import com.pixel.applock.ui.theme.PixelAppLockTheme

class AppSelectionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val pm = packageManager
        val resolveInfos = pm.queryIntentActivities(intent, 0)

        val apps = resolveInfos.mapNotNull { resolveInfo ->
            val appInfo = resolveInfo.activityInfo.applicationInfo
            val appName = resolveInfo.loadLabel(pm).toString()
            val packageName = resolveInfo.activityInfo.packageName
            val icon = resolveInfo.loadIcon(pm)
            AppInfo(name = appName, packageName = packageName, icon = icon)
        }.sortedBy { it.name }

        setContent {
            PixelAppLockTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppSelectionScreen(apps = apps, onSave = {
                        AppLockPrefs.setLockedApps(this, it)
                        finish()
                    })
                }
            }
        }
    }
}

@Composable
fun AppSelectionScreen(apps: List<AppInfo>, onSave: (Set<String>) -> Unit) {
    val selectedApps = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select apps to lock", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(apps) { app ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Image(
                            bitmap = app.icon.toBitmap().asImageBitmap(),
                            contentDescription = app.name,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(app.name, style = MaterialTheme.typography.bodyLarge)
                    }
                    Checkbox(
                        checked = selectedApps.contains(app.packageName),
                        onCheckedChange = {
                            if (it) selectedApps.add(app.packageName)
                            else selectedApps.remove(app.packageName)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onSave(selectedApps.toSet()) }, modifier = Modifier.fillMaxWidth()) {
            Text("Save Selection")
        }
    }
}

// Extension function to convert Drawable to Bitmap for Compose Image
fun android.graphics.drawable.Drawable.toBitmap(): android.graphics.Bitmap {
    if (this is android.graphics.drawable.BitmapDrawable) {
        if (bitmap != null) {
            return bitmap
        }
    }
    val bitmap = android.graphics.Bitmap.createBitmap(
        intrinsicWidth.coerceAtLeast(1),
        intrinsicHeight.coerceAtLeast(1),
        android.graphics.Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}
