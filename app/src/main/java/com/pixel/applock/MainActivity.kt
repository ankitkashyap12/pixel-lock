package com.pixel.applock

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pixel.applock.ui.AppSelectionActivity
import com.pixel.applock.ui.theme.PixelAppLockTheme
import com.pixel.applock.utils.BiometricUtils

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PixelAppLockTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    SetupFlowScreen(onFinishSetup = {
                        // Navigate to App Selection screen after setup is complete
                        startActivity(Intent(this, AppSelectionActivity::class.java))
                        finish()
                    })
                }
            }
        }
    }
}

@Composable
fun SetupFlowScreen(onFinishSetup: () -> Unit) {
    val context = LocalContext.current
    val hasBiometricSupport = remember {
        BiometricUtils.isBiometricAvailable(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Pixel AppLock", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            // Proceed to biometric or PIN setup
            context.startActivity(Intent(context, SecuritySetupActivity::class.java))
        }) {
            Text("Set PIN / Biometric")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            onFinishSetup()
        }) {
            Text("Select Apps to Lock")
        }
    }
}
