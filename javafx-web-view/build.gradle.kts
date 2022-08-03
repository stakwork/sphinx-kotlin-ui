import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.0.1"
    id("org.openjfx.javafxplugin") version "0.0.10"
}

group = "chat.sphinx"
version = "1.0"

javafx {
    version = "18.0.2"
    modules = listOf(
        "javafx.swing",
        "javafx.web"
    )
}

repositories {
    google()
    mavenCentral()
    maven(url = "https://jitpack.io")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_15.toString()
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            val kmpTorBinaryVersion = "0.4.7.8"

            dependencies {
                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.desktop.currentOs)
                api(compose.preview)
//                implementation ("com.github.skydoves:landscapist-glide:1.3.6")
//                implementation ("io.coil-kt:coil-compose:1.4.0")
            }
        }
        val jvmTest by getting
    }
}