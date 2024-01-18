plugins {
    id("org.jetbrains.compose") version "1.5.1"
    id("com.android.application")
    kotlin("android")
}

group = "chat.sphinx"
version = "1.0"

repositories {
//    jcenter()
}

dependencies {
    implementation(project(":common"))
    implementation("androidx.activity:activity-compose:1.8.0")
}

android {
    compileSdkVersion(31)
    defaultConfig {
        applicationId = "chat.sphinx.android"
        minSdkVersion(24)
        targetSdkVersion(31)
        versionCode = 1
        versionName = "1.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}