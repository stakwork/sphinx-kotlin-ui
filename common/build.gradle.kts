import org.jetbrains.compose.compose

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.5.1"
}

group = "chat.sphinx"
version = "1.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
    maven(url = "https://s01.oss.sonatype.org/content/repositories/snapshots")
}

kotlin {
    androidTarget()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
    }
    sourceSets {
        val klockVersion = "2.5.1"
        val korauVersion = "3.2.0"
        val korioVersion = "3.2.0"

        val commonMain by getting {
            dependencies {
                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(compose.foundation)
                api(compose.material)
                implementation(compose.materialIconsExtended)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.material3)
                api(project(":sphinx-kotlin-core"))
                implementation("com.soywiz.korlibs.klock:klock:$klockVersion")
                implementation("media.kamel:kamel-image:0.7.3")
                implementation("com.google.zxing:core:3.5.0")
                implementation("io.ktor:ktor-client-core:2.3.7")
                implementation("io.ktor:ktor-client-cio:2.3.7")
                implementation("com.russhwolf:multiplatform-settings:0.8.1")
                implementation("org.cryptonode.jncryptor:jncryptor:1.2.0")
                implementation("com.soywiz.korlibs.korio:korio:$korioVersion")
                implementation("com.soywiz.korlibs.korau:korau:$korauVersion")
                api("io.github.kevinnzou:compose-webview-multiplatform:1.8.0-SNAPSHOT")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.6.1")
                api("androidx.core:core-ktx:1.12.0")
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation("junit:junit:4.13")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                api(compose.desktop.common)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                api(compose.desktop.components.splitPane)
            }
        }
        val desktopTest by getting
    }
}

android {
    compileSdkVersion(31)
    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
            res.srcDirs("src/androidMain/res", "src/commonMain/resources")
        }
    }
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(31)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
dependencies {
    implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
    implementation("androidx.compose.ui:ui-text:1.5.4")
    implementation("com.google.android.material:material:1.10.0")
    implementation ("io.coil-kt:coil-compose:1.4.0")
    implementation("androidx.compose.material:material:1.6.0-alpha08")
}
