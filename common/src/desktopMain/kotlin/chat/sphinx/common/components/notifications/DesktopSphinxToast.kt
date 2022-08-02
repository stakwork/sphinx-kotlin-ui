package chat.sphinx.common.components.notifications

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.notifications.DesktopSphinxNotificationManager.toast

/**
 * This is a hack to have notifications on all platforms... Could possibly only use it for linux as the mac/windows notifications should work will.
 */
@Composable
fun DesktopSphinxToast(windowTitle: String) {
    when (val value = toast.value) {
        is SphinxToast -> {
            if (value.windowTitle == windowTitle) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 70.dp)
                    .background(color = Color.Transparent))
                {
                    Box(
                        modifier = Modifier
                            .background(value.color, RoundedCornerShape(5.dp))
                            .wrapContentSize(align = Alignment.Center)
                            .padding(10.dp, 5.dp)
                            .align(Alignment.BottomCenter)
                    ) {
                        Text(
                            text = value.message,
                            fontWeight = FontWeight.Normal,
                            fontFamily = Roboto,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            maxLines = 4,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}