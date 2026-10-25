plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.actionlock.free"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.actionlock.free"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0-FREE"
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    // On-device AI Pose Detection (Google ML Kit)
    implementation("com.google.mlkit:pose-detection:18.0.0-beta4")
}
