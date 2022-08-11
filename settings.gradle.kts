pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
        mavenCentral()
        maven(url = "https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.squareup.sqldelight") {
                useModule("com.squareup.sqldelight:gradle-plugin:1.5.1")
            }
        }
    }
}
rootProject.name = "sphinx-kotlin-ui"


include(":android")
include(":desktop")
include(":common")
include(":javafx-web-view")
include(":sphinx-kotlin-core")

