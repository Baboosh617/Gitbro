plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.gitbro"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gitbro"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    // Enables Jetpack Compose
    buildFeatures {
        compose = true
    }

    composeOptions {
        // Must match your Kotlin version (1.9.23 → compiler 1.5.11)
        kotlinCompilerExtensionVersion = "1.5.11"
    }
}

dependencies {
    // ── Jetpack Compose ──────────────────────────────────────────────────────
    // BOM = Bill of Materials: sets all Compose library versions consistently
    // so you never have version mismatches between compose libraries
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")     // Material 3 components
    implementation("androidx.activity:activity-compose:1.8.2") // setContent {}
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // ── Navigation ───────────────────────────────────────────────────────────
    // Handles moving between HomeScreen and ProfileScreen
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // ── Networking ───────────────────────────────────────────────────────────
    // Retrofit: turns API calls into simple Kotlin function calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson converter: automatically converts JSON → Kotlin data classes
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // ── Coroutines ───────────────────────────────────────────────────────────
    // Android's modern async/threading solution (replaces SwingWorker)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // ── Core Android ─────────────────────────────────────────────────────────
    implementation("androidx.core:core-ktx:1.12.0")
}