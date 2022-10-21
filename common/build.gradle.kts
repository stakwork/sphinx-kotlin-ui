import org.jetbrains.compose.compose

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.0.1"
    kotlin("plugin.serialization") version "1.6.10"

}

group = "chat.sphinx"
version = "1.0"

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_15.toString()
        }
    }
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_15.toString()
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
                implementation("com.alialbaali.kamel:kamel-image:0.3.0")
                implementation("com.google.zxing:core:3.5.0")
                implementation("com.russhwolf:multiplatform-settings:0.8.1")
                implementation("org.cryptonode.jncryptor:jncryptor:1.2.0")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
                implementation("com.soywiz.korlibs.korio:korio:$korioVersion")
                implementation("com.soywiz.korlibs.korau:korau:$korauVersion")
//                implementation ("com.google.accompanist:accompanist-pager:0.23.1")
//                implementation ("com.github.skydoves:landscapist-glide:1.3.6")
//                implementation ("com.google.accompanist:accompanist-flowlayout:0.24.12-rc")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.3.1")
                api("androidx.core:core-ktx:1.3.1")
            }
        }
        val androidTest by getting {
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

                api(project(":javafx-web-view"))
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
        sourceCompatibility = JavaVersion.VERSION_15
        targetCompatibility = JavaVersion.VERSION_15
    }
}
dependencies {
    implementation("androidx.compose.ui:ui-tooling-preview:1.1.1")
    implementation("androidx.compose.ui:ui-text:1.1.1")
    implementation("com.google.android.material:material:1.6.1")
    implementation ("io.coil-kt:coil-compose:1.4.0")
    implementation("androidx.compose.material:material:1.0.0-beta04")
}
