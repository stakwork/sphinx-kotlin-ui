package chat.sphinx.signup

import CommonButton
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.viewmodel.SignUpViewModel
import chat.sphinx.wrapper.message.media.isImage
import kotlinx.coroutines.launch
import theme.md_theme_dark_onBackground
import utils.deduceMediaType

@Composable
actual fun SignupUploadImageButton(viewModel: SignUpViewModel) {
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.height(48.dp).width(259.dp)) {
        Card(
            modifier = Modifier.wrapContentSize(),
            shape = RoundedCornerShape(23.dp),
            border = BorderStroke(1.dp, md_theme_dark_onBackground)
        ) {
            CommonButton(
                text = if (viewModel.signupBasicInfoState.userPicture == null) "Select Image" else "Change image",
                enabled = true,
                endIcon = Icons.Default.CameraAlt,
                backgroundColor = MaterialTheme.colorScheme.surface,
            ) {
                scope.launch {
                    ContentState.sendFilePickerDialog.awaitResult()?.let { path ->
                        if (path.deduceMediaType().isImage) {
                            viewModel.onProfilePictureChanged(path)
                        }
                    }
                }
            }
        }
    }
}