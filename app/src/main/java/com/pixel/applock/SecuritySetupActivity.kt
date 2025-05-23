package com.pixel.applock

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.pixel.applock.ui.AppSelectionActivity
import com.pixel.applock.ui.theme.PixelAppLockTheme

class SecuritySetupActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PixelAppLockTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SecuritySetupScreen()
                }
            }
        }
    }
}

@Composable
fun SecuritySetupScreen() {
    val context = LocalContext.current
    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    val isPinValid = pin.length >= 4

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Set Security PIN", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = { pin = it },
            label = { Text("Enter PIN") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPin,
            onValueChange = { confirmPin = it },
            label = { Text("Confirm PIN") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (!isPinValid) {
                    Toast.makeText(context, "PIN must be at least 4 digits", Toast.LENGTH_SHORT).show()
                } else if (pin != confirmPin) {
                    Toast.makeText(context, "PINs do not match", Toast.LENGTH_SHORT).show()
                } else {
                    // Save PIN and proceed
                    AppLockPrefs.savePin(context, pin)

                    context.startActivity(Intent(context, AppSelectionActivity::class.java))
                    (context as? ComponentActivity)?.finish()
                }
            },
            enabled = isPinValid
        ) {
            Text("Continue")
        }
    }
}
