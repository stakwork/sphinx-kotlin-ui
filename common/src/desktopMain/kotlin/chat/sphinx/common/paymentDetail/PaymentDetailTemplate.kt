package chat.sphinx.common.paymentDetail

import Roboto
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.PhotoFileImage
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.common.viewmodel.chat.payment.PaymentViewModel
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.wrapper.PhotoUrl
import okio.Path.Companion.toOkioPath

@Composable
fun PaymentDetailTemplate(
    chatViewModel: ChatViewModel,
    viewModel: PaymentViewModel
) {
    Box(
        modifier = Modifier
            .width(440.dp)
            .height(580.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(10.dp)
            ).clickable {},
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(10.dp)
                        .clickable {
                            chatViewModel.toggleChatActionsPopup(
                                ChatViewModel.ChatActionsMode.SEND_AMOUNT, viewModel.getPaymentData()
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "PAYMENT TEMPLATE",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = SphinxFonts.montserratFamily,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(10.dp)
                        .clickable {
                            chatViewModel.hideChatActionsPopup()
                        },
                    contentAlignment = Alignment.Center

                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viewModel.chatPaymentState.amount?.toString() ?: "",
                    color = MaterialTheme.colorScheme.tertiary,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "sats",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontFamily = Roboto,
                    fontWeight = FontWeight.Light,
                    fontSize = 20.sp,
                )

            }
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (viewModel.chatPaymentState.message.isEmpty()) {
                    Text(
                        text = "No message",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 14.sp
                    )
                } else {
                    Text(
                        text = viewModel.chatPaymentState.message,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            val templateList by viewModel.paymentTemplateList.collectAsState()

            Box(
                modifier = Modifier.fillMaxWidth().height(260.dp),
                contentAlignment = Alignment.Center
            ) {
                if (!templateList.isNullOrEmpty()) {
                    PhotoFileImage(
                        photoFilepath = templateList!![2].localFile?.toOkioPath()!!,
                        modifier = Modifier.height(250.dp).width(200.dp)
                    )
                }
            }
            Box(
                modifier = Modifier.fillMaxWidth().height(70.dp).padding(start = 100.dp)
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    state = rememberLazyListState()
                ) {
                    item {
                    }
                    templateList?.size?.let {
                        items(it) { index ->
                            templateList?.get(index)?.localFile?.toOkioPath()?.let { path ->
                                PhotoFileImage(
                                    photoFilepath = path,
                                    modifier = Modifier
                                        .size(60.dp).padding(8.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
        Column(
            modifier = Modifier.fillMaxSize().padding(bottom = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Button(
                onClick = {
                          viewModel.sendContactPayment()
                },
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth(0.5f).height(50.dp)
            ) {
                Text("Confirm", color = MaterialTheme.colorScheme.tertiary, fontSize = 16.sp)
            }
        }
    }
}