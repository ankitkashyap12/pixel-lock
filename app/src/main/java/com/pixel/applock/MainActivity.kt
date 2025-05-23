// File: MainActivity.kt

package com.pixel.applock

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pixel.applock.ui.AppLockTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppLockTheme {
                AppSelectorScreen()
            }
        }
    }
}
