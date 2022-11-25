package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.common.viewmodel.chat.payment.PaymentViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.wrapper.PhotoUrl
import theme.light_divider

@Composable
fun TribeProfilePopUp(
    chatViewModel: ChatViewModel,
    paymentViewModel: PaymentViewModel
) {
    val viewModel = remember { chatViewModel }
    val viewState = viewModel.tribeProfileState

    Box(
        modifier = Modifier
            .width(420.dp)
            .height(525.dp)
            .background(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(
                onClick = {},
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        if (viewState.loadingTribeProfile) {
            Box(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    Modifier.size(40.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            }
        } else {
            Column(
                Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.background(MaterialTheme.colorScheme.onSurfaceVariant),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        PhotoUrlImage(
                            photoUrl = PhotoUrl(viewState.profilePicture),
                            modifier = Modifier
                                .size(112.dp)
                                .clip(CircleShape)
                        )

                        Spacer(modifier = Modifier.width(16.dp))
                        Column() {
                            Text(
                                text = "TRIBE MEMBER",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Roboto,
                                fontSize = 10.sp
                            )
                            Spacer(Modifier.width(2.dp))
                            Text(
                                text = viewState.name,
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Roboto,
                                fontSize = 22.sp
                            )
                            Spacer(Modifier.width(6.dp))
                            Column(
                                modifier = Modifier.height(40.dp).padding(top = 4.dp)
                            ) {
                                Text(
                                    text = viewState.description,
                                    maxLines = 2,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    fontFamily = SphinxFonts.montserratFamily,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(26.dp))
                SendSatsButton(chatViewModel, paymentViewModel)
                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().height(49.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Price to Meet:",
                        fontSize = 15.sp,
                        fontFamily = Roboto,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = viewState.priceToMeet,
                            fontSize = 15.sp,
                            fontFamily = Roboto,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
                Divider(modifier = Modifier.fillMaxWidth(), color = light_divider)

                Row(
                    modifier = Modifier.fillMaxWidth().height(49.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Coding languages:",
                        fontSize = 15.sp,
                        fontFamily = Roboto,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = viewState.codingLanguages,
                            fontSize = 15.sp,
                            fontFamily = Roboto,
                            maxLines = 2,
                            textAlign = TextAlign.End,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
                Divider(modifier = Modifier.fillMaxWidth(), color = light_divider)

                Row(
                    modifier = Modifier.fillMaxWidth().height(49.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Posts:",
                        fontSize = 15.sp,
                        fontFamily = Roboto,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = viewState.posts,
                            fontSize = 15.sp,
                            fontFamily = Roboto,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
                Divider(modifier = Modifier.fillMaxWidth(), color = light_divider)

                Row(
                    modifier = Modifier.fillMaxWidth().height(49.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Twitter:",
                        fontSize = 15.sp,
                        fontFamily = Roboto,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = viewState.twitter,
                            fontSize = 15.sp,
                            fontFamily = Roboto,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
                Divider(modifier = Modifier.fillMaxWidth(), color = light_divider)

                Row(
                    modifier = Modifier.fillMaxWidth().height(49.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Github:",
                        fontSize = 15.sp,
                        fontFamily = Roboto,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = viewState.github,
                            fontSize = 15.sp,
                            fontFamily = Roboto,
                            color = MaterialTheme.colorScheme.tertiary,
                        )
                    }
                }
                Divider(modifier = Modifier.fillMaxWidth(), color = light_divider)
            }
        }
    }
}

@Composable
private fun SendSatsButton(
    chatViewModel: ChatViewModel,
    paymentViewModel: PaymentViewModel
) {
    Button(
        modifier = Modifier.width(147.dp).height(40.dp),
        onClick = {
            chatViewModel.setChatActionsStateFlow(
                Pair(
                    ChatViewModel.ChatActionsMode.SEND_AMOUNT, paymentViewModel.getPaymentData()
                )
            )
        },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colorScheme.tertiary
        ),
        border = BorderStroke(1.dp, light_divider),
    ) {
        Box {
            Text(
                "Send Sats",
                color = Color.Black,
                fontFamily = Roboto,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                textAlign = TextAlign.Center
            )
            Image(
                painter = imageResource(Res.drawable.ic_send),
                contentDescription = "",
                modifier = Modifier.align(Alignment.CenterEnd).size(10.dp)
            )
        }
    }
}

