import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.FileInputStream
import java.util.*

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.5.1"
}

group = "chat.sphinx"
version = "1.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            val kmpTorBinaryVersion = "0.4.7.8"
            val korauVersion = "3.2.0"
            val korioVersion = "3.2.0"

            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
                api(compose.preview)

                implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-linuxx64:$kmpTorBinaryVersion")
                implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-macosx64:$kmpTorBinaryVersion")
                implementation("io.matthewnelson.kotlin-components:kmp-tor-binary-mingwx64:$kmpTorBinaryVersion")
                implementation("com.soywiz.korlibs.korio:korio:$korioVersion")
                implementation("com.soywiz.korlibs.korau:korau:$korauVersion")
                implementation("org.jetbrains.compose.ui:ui-graphics:1.1.1")
                implementation("uk.co.caprica:vlcj:4.7.1")


//                implementation ("com.github.skydoves:landscapist-glide:1.3.6")
//                implementation ("io.coil-kt:coil-compose:1.4.0")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {

    application {
        mainClass = "MainKt"
        nativeDistributions {
            // Modules suggested by suggestRuntimeModules (avoids the ClassNotFoundException)
            modules("java.instrument", "java.management", "java.prefs", "java.sql", "jdk.unsupported")

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Sphinx"
            packageVersion = "1.0.17"

            val iconsRoot = project.file("../common/src/desktopMain/resources/images")
            val sphinxProperties = Properties().apply {
                val localPropertiesFile = project.file("../local.properties")
                if (localPropertiesFile.exists()) {
                    load(FileInputStream(localPropertiesFile))
                }
            }

            val macOsBundleID = sphinxProperties.getProperty("macOs.bundleID")
            val macOsSigningIdentity = sphinxProperties.getProperty("macOs.signing.identity")

            macOS {
                if (macOsBundleID?.isNotEmpty() == true) {
                    bundleID = macOsBundleID
                }

                signing {
                    if (macOsSigningIdentity?.isNotEmpty() == true) {
                        sign.set(true)
                        identity.set(macOsSigningIdentity)
                    }
                }
                iconFile.set(iconsRoot.resolve("sphinx-logo.icns"))
            }
            windows {
                iconFile.set(iconsRoot.resolve("sphinx-logo-64.png"))
                dirChooser = true
            }
            linux {
                iconFile.set(iconsRoot.resolve("sphinx-logo.png"))
            }
        }
    }
}