package chat.sphinx.common.components.pin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.viewmodel.PINHandlingViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.onKeyUp
import chat.sphinx.utils.SphinxFonts
import theme.badge_red
import theme.primary_green
import utils.AnimatedContainer
import androidx.compose.ui.text.style.TextAlign

@Composable
actual fun PINScreen(
    pinHandlingViewModel: PINHandlingViewModel,
    descriptionMessage: String?,
    successMessage: String?,
    errorMessage: String?
) {
    Text(
        text = "Pin Screen"
    )
}