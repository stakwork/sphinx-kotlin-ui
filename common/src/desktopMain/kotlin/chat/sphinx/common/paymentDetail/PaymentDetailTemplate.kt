package chat.sphinx.common.paymentDetail

import Roboto
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.PhotoFileImage
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.common.viewmodel.chat.payment.PaymentViewModel
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.wrapper.payment.PaymentTemplate
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import chat.sphinx.common.Res
import chat.sphinx.platform.imageResource
import chat.sphinx.response.LoadResponse
import okio.Path.Companion.toOkioPath
import theme.badge_red
import theme.template_circle_background

@Composable
fun PaymentDetailTemplate(
    chatViewModel: ChatViewModel,
    viewModel: PaymentViewModel
) {
    Box(
        modifier = Modifier
            .width(455.dp)
            .height(580.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(10.dp)
            ).clickable(
                onClick = {},
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                        tint = badge_red,
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
            val selectedTemplate by viewModel.selectedTemplate.collectAsState()

            if (templateList == null || templateList?.isEmpty() == true) {
                Box(modifier = Modifier.fillMaxWidth().height(305.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(50.dp)
                    )
                }
            } else {
                val listState = rememberLazyListState()

                val templates = mutableStateListOf<PaymentTemplate>()
                templates.addAll(templateList!!)

                Box(
                    modifier = Modifier.fillMaxWidth().height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedTemplate != null && selectedTemplate?.localFile != null) {
                        PhotoFileImage(
                            photoFilepath = selectedTemplate!!.localFile!!.toOkioPath(),
                            modifier = Modifier.height(200.dp).fillMaxWidth(),
                            contentScale = ContentScale.Inside
                        )
                    } else {
                        Image(
                            painter = imageResource(Res.drawable.ic_template_no_image),
                            contentDescription = "No image",
                            modifier = Modifier.height(200.dp).fillMaxWidth(),
                            contentScale = ContentScale.Inside
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth().height(65.dp)
                ) {
                    LazyRow(
                        modifier = Modifier.fillMaxSize(),
                        state = listState
                    ) {
                        item {
                            Box(modifier = Modifier.size(195.dp)) {}
                        }
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(65.dp)
                                    .padding(8.dp)
                                    .clip(CircleShape)
                                    .background(color = template_circle_background)
                                    .clickable {
                                        viewModel.selectTemplate(-1)
                                    }
                            ) {
                                Image(
                                    painter = imageResource(Res.drawable.ic_template_no_image),
                                    contentDescription = "No image",
                                    modifier = Modifier
                                        .width(35.dp)
                                        .height(35.dp),
                                    contentScale = ContentScale.Inside
                                )
                            }
                        }
                        itemsIndexed(
                            templates,
                            key = { _, item -> "${item.muid}" }
                        ) { index, paymentTemplate ->
                            paymentTemplate?.localFile?.toOkioPath()?.let { path ->
                                PhotoFileImage(
                                    photoFilepath = path,
                                    modifier = Modifier
                                        .size(65.dp).padding(8.dp)
                                        .clip(CircleShape)
                                        .background(color = template_circle_background)
                                        .clickable {
                                            viewModel.selectTemplate(index)
                                        }
                                )
                            }
                        }
                        item {
                            Box(modifier = Modifier.size(195.dp)) {}
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .width(180.dp)
                    .height(80.dp)
            ) {
                Button(
                    onClick = {
                        viewModel.sendContactPayment()
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Confirm", color = MaterialTheme.colorScheme.tertiary, fontSize = 16.sp)
                }
                if (viewModel.chatPaymentState.status is LoadResponse.Loading) {
                    CircularProgressIndicator(
                        Modifier.padding(start = 8.dp).size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                }
            }
        }
    }
}