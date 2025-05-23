package com.pixel.applock

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pixel.applock.ui.AppSelectionActivity
import com.pixel.applock.ui.theme.PixelAppLockTheme
import kotlinx.coroutines.delay

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
    var currentStep by remember { mutableStateOf(0) } // 0 = enter PIN, 1 = confirm PIN
    var hasNavigated by remember { mutableStateOf(false) }

    val isPinValid = pin.length == 6
    val isConfirmValid = confirmPin.length == 6
    val pinsMatch = pin == confirmPin && isPinValid && isConfirmValid

    // Debug info - remove this later
    val debugInfo = "PIN: '$pin' (${pin.length}), Confirm: '$confirmPin' (${confirmPin.length}), Match: $pinsMatch"

    // Auto-advance to confirmation step when PIN is complete
    LaunchedEffect(pin) {
        if (pin.length == 6 && currentStep == 0) {
            delay(300)
            currentStep = 1
        }
    }

    // Handle successful PIN match
    LaunchedEffect(confirmPin) {
        if (confirmPin.length == 6 && pin.length == 6) {
            delay(100) // Small delay to ensure UI updates
            if (pin == confirmPin && !hasNavigated) {
                hasNavigated = true
                println("DEBUG: PINs match, attempting navigation...")

                try {
                    // Save the PIN
                    AppLockPrefs.savePin(context, pin)
                    println("DEBUG: PIN saved successfully")

                    // Navigate to app selection - try multiple approaches
                    val intent = Intent().apply {
                        setClassName(context.packageName, "com.pixel.applock.ui.AppSelectionActivity")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }

                    println("DEBUG: Starting activity with intent: $intent")
                    context.startActivity(intent)
                    println("DEBUG: Activity started successfully")

                    // Finish current activity after a small delay
                    delay(100)
                    (context as? ComponentActivity)?.finish()
                    println("DEBUG: Current activity finished")

                } catch (e: Exception) {
                    println("DEBUG: Navigation failed with error: ${e.message}")
                    e.printStackTrace()

                    // Try alternative navigation method
                    try {
                        val fallbackIntent = Intent(context, Class.forName("com.pixel.applock.ui.AppSelectionActivity"))
                        context.startActivity(fallbackIntent)
                        (context as? ComponentActivity)?.finish()
                        println("DEBUG: Fallback navigation succeeded")
                    } catch (e2: Exception) {
                        println("DEBUG: Fallback navigation also failed: ${e2.message}")
                        hasNavigated = false // Reset to allow retry
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set Security PIN",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        when (currentStep) {
            0 -> {
                InteractivePinInput(
                    value = pin,
                    onValueChange = { newValue ->
                        if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                            pin = newValue
                        }
                    },
                    label = "Enter 6-digit PIN"
                )
            }
            1 -> {
                // Show completed PIN (read-only)
                PinDisplay(
                    value = pin,
                    label = "PIN Set"
                )

                Spacer(modifier = Modifier.height(24.dp))

                InteractivePinInput(
                    value = confirmPin,
                    onValueChange = { newValue ->
                        if (newValue.length <= 6 && newValue.all { it.isDigit() }) {
                            confirmPin = newValue
                        }
                    },
                    label = "Confirm PIN"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Show error if PINs don't match
                if (confirmPin.length == 6 && pin != confirmPin) {
                    Text(
                        text = "PINs do not match",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to restart",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.clickable {
                            pin = ""
                            confirmPin = ""
                            currentStep = 0
                        }
                    )
                }
            }
        }

        // Success message
        if (pinsMatch) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "PIN set successfully! Navigating...",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Debug info - remove this after testing
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = debugInfo,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun InteractivePinInput(
    value: String,
    onValueChange: (String) -> Unit,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Clickable container that wraps both the PIN display and the text field
        Box(
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { /* Handle click if needed */ }
        ) {
            // PIN dots display
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    val isFilled = index < value.length
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isFilled) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isFilled) {
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.headlineLarge,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Invisible BasicTextField that covers the entire PIN area
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                singleLine = true,
                cursorBrush = SolidColor(Color.Transparent), // Hide cursor
                decorationBox = { /* Empty decoration box to make it invisible */ }
            )
        }
    }
}

@Composable
fun PinDisplay(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // PIN dots display (read-only)
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) { index ->
                val isFilled = index < value.length
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isFilled) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isFilled) {
                        Text(
                            text = "•",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}