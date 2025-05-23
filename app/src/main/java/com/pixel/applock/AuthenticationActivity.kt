package com.pixel.applock

import android.os.Bundle
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.pixel.applock.ui.theme.PixelAppLockTheme

class AuthenticationActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val composeView = ComposeView(this).apply {
            setContent {
                PixelAppLockTheme {
                    Surface(color = MaterialTheme.colorScheme.background) {
                        AuthenticationScreen(onAuthenticated = {
                            // TODO: Proceed to app selection or main screen
                        })
                    }
                }
            }
        }
        setContentView(composeView)
    }
}

@Composable
fun AuthenticationScreen(onAuthenticated: () -> Unit) {
    val context = LocalContext.current
    val biometricManager = BiometricManager.from(context)

    LaunchedEffect(Unit) {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                showPrompt(context as FragmentActivity, onAuthenticated)
            }

            else -> {
                // TODO: Navigate to fallback authentication (PIN/Pattern)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Authenticating...", style = MaterialTheme.typography.titleMedium)
    }
}

fun showPrompt(activity: FragmentActivity, onSuccess: () -> Unit) {
    val executor = ContextCompat.getMainExecutor(activity)
    val promptInfo = BiometricPrompt.PromptInfo.Builder().setTitle("Biometric Authentication")
        .setSubtitle("Verify your identity")
        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
        .build()

    val prompt =
        BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                activity.finish()
            }
        })

    prompt.authenticate(promptInfo)
}