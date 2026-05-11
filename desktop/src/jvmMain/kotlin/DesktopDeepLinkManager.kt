import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.awt.Desktop

object DesktopDeepLinkManager {
    private const val SPHINX_SCHEME = "sphinx.chat://"
    private const val HTTP_PREFIX = "http://"
    private const val HTTPS_PREFIX = "https://"

    private val _deepLink: MutableStateFlow<String?> = MutableStateFlow(null)
    val deepLink: StateFlow<String?> = _deepLink

    fun register(args: Array<String>) {
        args.mapNotNull(::normalizeDeepLink).firstOrNull()?.let(::open)
        registerOpenUriHandler()
    }

    fun clear(link: String) {
        if (_deepLink.value == link) {
            _deepLink.value = null
        }
    }

    private fun open(link: String) {
        _deepLink.value = link
    }

    private fun registerOpenUriHandler() {
        if (!Desktop.isDesktopSupported()) {
            return
        }

        try {
            Desktop.getDesktop().setOpenURIHandler { event ->
                normalizeDeepLink(event.uri.toString())?.let(::open)
            }
        } catch (e: UnsupportedOperationException) {
            println("Deep link handler is not supported on this platform")
        } catch (e: SecurityException) {
            println("Deep link handler registration was blocked")
        }
    }

    private fun normalizeDeepLink(value: String): String? {
        val trimmedValue = value.trim()
        return when {
            trimmedValue.startsWith(SPHINX_SCHEME) -> trimmedValue
            trimmedValue.startsWith(HTTP_PREFIX + SPHINX_SCHEME) ->
                trimmedValue.removePrefix(HTTP_PREFIX)
            trimmedValue.startsWith(HTTPS_PREFIX + SPHINX_SCHEME) ->
                trimmedValue.removePrefix(HTTPS_PREFIX)
            else -> null
        }
    }
}
