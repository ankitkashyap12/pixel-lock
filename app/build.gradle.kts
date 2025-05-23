plugins {
    id("com.android.application")
//    id("org.jetbrains.kotlin.android")
    kotlin("android")
}

android {
    namespace = "com.pixel.applock"
    compileSdk = 34

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17 // Set Java source compatibility to 17
        targetCompatibility = JavaVersion.VERSION_17 // Set Java target compatibility to 17
    }

    defaultConfig {
        applicationId = "com.pixel.applock"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose UI
    implementation("androidx.compose.ui:ui:1.6.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.1")

    // Material 3
    implementation("androidx.compose.material3:material3:1.2.0")

    // Optional: Debug preview tools
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.1")
    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("androidx.preference:preference-ktx:1.2.0")
    // Add this for BiometricPrompt
    implementation(libs.androidx.biometric.ktx.v120alpha05) // Or latest stable version (uses -ktx for coroutine support)


}
