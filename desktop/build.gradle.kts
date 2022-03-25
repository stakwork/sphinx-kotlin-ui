import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.0.1"
}

group = "chat.sphinx"
version = "1.0"

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            val kmpTorVersion = "0.4.6.10+0.1.0-beta1"

            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
                api(compose.preview)

//                implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-linuxx64:$kmpTorVersion")
//                implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-macosx64:$kmpTorVersion")
//                implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-mingwx64:$kmpTorVersion")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Sphinx"
            packageVersion = "1.0.0"

            val iconsRoot = project.file("../common/src/desktopMain/resources/images")
            macOS {
                iconFile.set(iconsRoot.resolve("sphinx-logo.icns"))
            }
            windows {
                iconFile.set(iconsRoot.resolve("sphinx-logo.ico"))
            }
            linux {
                iconFile.set(iconsRoot.resolve("sphinx-logo.png"))
            }
        }
    }
}