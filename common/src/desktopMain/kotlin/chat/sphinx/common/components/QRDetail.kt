package chat.sphinx.common.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.components.notifications.DesktopSphinxToast
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.contact.QRCodeViewModel
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.utils.toAnnotatedString

@Composable
fun QRDetail(
    dashboardViewModel: DashboardViewModel,
    viewModel: QRCodeViewModel
){
    val density = LocalDensity.current
    val clipboardManager: ClipboardManager = LocalClipboardManager.current

    var isOpen by remember { mutableStateOf(true) }
    if (isOpen) {
        Window(
            onCloseRequest = {
                dashboardViewModel.toggleQRWindow(false)
            },
            title = "QR Code",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(357, 550)
            ),
            resizable = false
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
                    .clickable {
                        clipboardManager.setText(viewModel.contactQRCodeState.string.toAnnotatedString())
                        viewModel.toast("Code copied to clipboard")
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 38.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = viewModel.contactQRCodeState.viewTitle.uppercase(),
                        fontFamily = SphinxFonts.montserratFamily,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(end = 24.dp)
                    ) {
                        Icon(
                            Icons.Default.TouchApp,
                            contentDescription = "QR Code",
                            tint = Color.Gray,
                            modifier = Modifier.size(30.dp)
                        )

                        Text(
                            text = "CLICK TO COPY",
                            fontFamily = SphinxFonts.montserratFamily,
                            color = Color.Gray,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(Modifier.height(18.dp))

                    viewModel.contactQRCodeState.bitMatrix?.let { bitMatrix ->
                        val width = bitMatrix.width
                        val height = bitMatrix.height

                        val widthDp = with(density) { width.toDp() }
                        val heightDp = with(density) { height.toDp() }

                        Box(modifier = Modifier.height(heightDp).width(widthDp)) {
                            Canvas(modifier = Modifier.height(heightDp).width(widthDp)) {
                                for (x in 0 until width) {
                                    for (y in 0 until height) {
                                        drawRect(
                                            brush = SolidColor(if (bitMatrix.get(x, y)) Color.Black else Color.White),
                                            topLeft = Offset(x.toFloat(), y.toFloat()),
                                            size = Size(1f, 1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                        text = viewModel.contactQRCodeState.string,
                        maxLines = 2,
                        fontFamily = SphinxFonts.montserratFamily,
                        color = Color.Gray,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            DesktopSphinxToast("QR Code")
        }
    }
}

