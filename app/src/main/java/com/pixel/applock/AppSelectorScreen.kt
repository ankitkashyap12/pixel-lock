// File: AppSelectorScreen.kt

package com.pixel.applock

import android.content.pm.PackageManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.launch

@Composable
fun AppSelectorScreen() {
    val context = LocalContext.current
    val pm = context.packageManager
    val apps = remember {
        pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .sortedBy { it.loadLabel(pm).toString() }
    }

    var lockedApps by remember { mutableStateOf<Set<String>>(emptySet()) }

    // Load saved locked apps
    val savedLockedAppsFlow = AppLockPrefs.getLockedApps(context)
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        savedLockedAppsFlow.collect { lockedApps = it }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Select Apps to Lock", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(apps) { app ->
                val isLocked = lockedApps.contains(app.packageName)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    val icon = app.loadIcon(pm).toBitmap().asImageBitmap()
                    Image(bitmap = icon, contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        app.loadLabel(pm).toString(),
                        modifier = Modifier.weight(1f)
                    )

                    Switch(
                        checked = isLocked,
                        onCheckedChange = { checked ->
                            val updated = if (checked) {
                                lockedApps + app.packageName
                            } else {
                                lockedApps - app.packageName
                            }
                            lockedApps = updated
                            // Launch coroutine to save the updated list
                            coroutineScope.launch {
                                AppLockPrefs.saveLockedApps(context, updated)
                                println(updated)
                            }
                        }
                    )
                }
            }
        }
    }
}
