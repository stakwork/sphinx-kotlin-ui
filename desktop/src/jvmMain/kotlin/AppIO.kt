
import androidx.compose.ui.res.useResource
import dev.datlag.tooling.canWriteSafely
import dev.datlag.tooling.existsRWSafely
import dev.datlag.tooling.mkdirsSafely
import dev.datlag.tooling.scopeCatching
import net.harawata.appdirs.AppDirsFactory
import org.apache.commons.lang3.SystemUtils
import java.io.File
import java.io.InputStream


object AppIO {

    private val dirs by lazy {
        AppDirsFactory.getInstance()
    }

//    fun applyTitle(title: String) = scopeCatching {
//        val toolkit = Toolkit.getDefaultToolkit()
//        val awtAppClassNameField = toolkit.javaClass.getDeclaredField("awtAppClassName")
//        val working = try {
//            awtAppClassNameField.isAccessible = true
//            awtAppClassNameField.canAccess(null)
//        } catch (ignored: Throwable) {
//            awtAppClassNameField.trySetAccessible()
//        }
//        awtAppClassNameField.set(toolkit, title)
//        working
//    }.getOrNull() ?: false
//
//    fun loadAppIcon(
//        window: ComposeWindow,
//        scope: CoroutineScope,
//        vararg assets: AssetResource
//    ) = scope.launchIO {
//        val appIcons = assets.map { async {
//            getAppImage(it)
//        } }.awaitAll().filterNotNull()
//
//        withMainContext {
//            window.iconImages = appIcons
//        }
//    }

//    private suspend fun getAppImage(asset: AssetResource): Image? = suspendCatching {
//        (getResourceAsInputStream(asset.filePath)
//            ?: getResourceAsInputStream(asset.originalPath)
//            ?: asset.resourcesClassLoader.getResourceAsStream(asset.filePath)
//                )?.use {
//                ImageIcon(it.readBytes()).image
//            }
//    }.getOrNull()

    private fun getResourceAsInputStream(location: String): InputStream? {
        val classLoader = AppIO::class.java.classLoader ?: this::class.java.classLoader
        return scopeCatching {
            classLoader?.getResourceAsStream(location)
        }.getOrNull() ?: scopeCatching {
            AppIO::class.java.getResourceAsStream(location)
        }.getOrNull() ?: scopeCatching {
            this::class.java.getResourceAsStream(location)
        }.getOrNull() ?: scopeCatching {
            useResource(location) { it }
        }.getOrNull()
    }

    fun getFileInConfigDir(name: String): File {
        val parentFile = File(dirs.getUserConfigDir(APP_NAME, null, null))
        var returnFile = File(parentFile, name)
        if (returnFile.existsRWSafely()
            || (returnFile.parentFile ?: parentFile).existsRWSafely()
            || (returnFile.parentFile ?: parentFile).mkdirsSafely()) {
            return returnFile
        } else if (SystemUtils.IS_OS_LINUX) {
            val configDir = File(homeDirectory(), ".config/$APP_NAME").apply {
                mkdirsSafely()
            }
            returnFile = File(configDir, name)
            return returnFile
        }
        return returnFile
    }

    fun getFileInUserDataDir(name: String): File {
        val parentFile = File(dirs.getUserDataDir(APP_NAME, null, null))
        var returnFile = File(parentFile, name)

        if (returnFile.existsRWSafely()
            || (returnFile.parentFile ?: parentFile).existsRWSafely()
            || (returnFile.parentFile ?: parentFile).mkdirsSafely()) {
            return returnFile
        } else if (SystemUtils.IS_OS_LINUX) {
            val dataDir = File(homeDirectory(), ".local/share/$APP_NAME").apply {
                mkdirsSafely()
            }
            returnFile = File(dataDir, name)
            return returnFile
        }
        return returnFile
    }

    fun getFileInSiteDataDir(name: String): File {
        val parentFile = File(dirs.getSiteDataDir(APP_NAME, null, null))
        var returnFile = File(parentFile, name)
        if (returnFile.existsRWSafely()
            || (returnFile.parentFile ?: parentFile).existsRWSafely()
            || (returnFile.parentFile ?: parentFile).mkdirsSafely()) {
            return returnFile
        } else if (SystemUtils.IS_OS_LINUX) {
            val dataDir = File("/usr/local/share/$APP_NAME")
            returnFile = File(dataDir, name)
            if (returnFile.existsRWSafely()
                || (returnFile.parentFile ?: dataDir).existsRWSafely()
                || (returnFile.parentFile ?: dataDir).mkdirsSafely()) {
                return returnFile
            }

            val alternativeDataDir = File(homeDirectory(), ".local/share/flatpak/exports/share/$APP_NAME").apply {
                mkdirsSafely()
            }
            returnFile = File(alternativeDataDir, name)
            return returnFile
        }
        return returnFile
    }

    fun getWriteableExecutableFolder(): File {
        val resDir = systemProperty("compose.application.resources.dir")?.let { File(it) }
        return if (resDir.existsRWSafely()) {
            resDir!!
        } else {
            if (File("./").canWriteSafely()) {
                File("./")
            } else {
                getFileInSiteDataDir("./")
            }
        }
    }

    private const val APP_NAME = "Sphinx"
}

fun systemProperty(key: String): String? = scopeCatching {
    System.getProperty(key).ifEmpty {
        null
    }
}.getOrNull()

fun systemProperty(key: String, value: String): String? = scopeCatching {
    System.setProperty(key, value).ifEmpty {
        null
    }
}.getOrNull()

fun systemEnv(key: String): String? = scopeCatching {
    System.getenv(key).ifEmpty {
        null
    }
}.getOrNull()

fun homeDirectory(): File? {
    return systemProperty("user.home")?.let {
        File(it)
    } ?: systemEnv("HOME")?.let {
        File(it)
    } ?: systemEnv("\$HOME")?.let {
        File(it)
    }
}