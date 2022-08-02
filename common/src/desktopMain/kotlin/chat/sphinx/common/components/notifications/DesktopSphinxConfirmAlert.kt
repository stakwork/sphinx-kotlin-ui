package chat.sphinx.common.components.notifications

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
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
import chat.sphinx.common.components.notifications.DesktopSphinxNotificationManager.alert
import chat.sphinx.common.components.notifications.DesktopSphinxNotificationManager.toast
import chat.sphinx.utils.SphinxFonts
import theme.badge_red
import theme.primary_green

/**
 * This is a hack to have notifications on all platforms... Could possibly only use it for linux as the mac/windows notifications should work will.
 */
@Composable
fun DesktopSphinxConfirmAlert(windowTitle: String) {
    when (val value = alert.value) {
        is SphinxAlertConfirm -> {
            if (value.windowTitle == windowTitle) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = Color.Black.copy(0.2f))
                        .clickable {  },
                    contentAlignment = Alignment.Center
                )
                {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(5.dp))
                            .wrapContentHeight(align = Alignment.CenterVertically)
                            .width(300.dp)
                            .padding(16.dp, 16.dp)
                    ) {
                        Text(
                            text = value.title,
                            fontWeight = FontWeight.Medium,
                            fontFamily = Roboto,
                            fontSize = 16.sp,
                            color = Color.Black,
                            maxLines = 1,
                            textAlign = TextAlign.Left,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = value.message,
                            fontWeight = FontWeight.Normal,
                            fontFamily = Roboto,
                            fontSize = 12.sp,
                            color = Color.Gray,
                            maxLines = 4,
                            textAlign = TextAlign.Left,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    alert.value = null
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = badge_red),
                                modifier = Modifier.wrapContentSize().padding(8.dp),
                            ) {
                                Text(
                                    "CANCEL",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontFamily = SphinxFonts.montserratFamily,
                                    fontSize = 12.sp
                                )
                            }
                            Button(
                                onClick = {
                                    value.confirmCallback?.invoke()
                                    alert.value = null
                                },
                                colors = ButtonDefaults.buttonColors(backgroundColor = primary_green),
                                modifier = Modifier.wrapContentSize().padding(8.dp),
                            ) {
                                Text(
                                    "CONFIRM",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontFamily = SphinxFonts.montserratFamily,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}