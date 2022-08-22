package chat.sphinx.common.components.browser

import androidx.compose.runtime.Composable

@Composable
expect fun AuthorizeBrowserDialog(onClose:()->Unit,onSubmit: ((String) -> Unit))