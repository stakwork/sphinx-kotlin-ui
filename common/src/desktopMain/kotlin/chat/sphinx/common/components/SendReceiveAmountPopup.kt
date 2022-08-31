package chat.sphinx.common.components

import CommonButton
import Roboto
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.common.viewmodel.chat.payment.PaymentViewModel
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.SphinxFonts
import theme.badge_red
import theme.light_divider
import theme.place_holder_text
import theme.primary_blue


@Composable
fun SendReceiveAmountPopup(
    chatViewModel: ChatViewModel,
    viewModel: PaymentViewModel
) {
    Box(
        modifier = Modifier
            .width(380.dp)
            .height(500.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable {},
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clickable {
                            chatViewModel.hideChatActionsPopup()
                        },
                    contentAlignment = Alignment.Center,
                ){
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "close",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        if (viewModel.mode == PaymentViewModel.PaymentMode.SEND) "SEND PAYMENT" else "REQUEST PAYMENT",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = SphinxFonts.montserratFamily,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Box(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                Column(modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Spacer(modifier = Modifier.width(66.dp))
                        OutlinedTextField(
                            value = viewModel.chatPaymentState.amount?.toString() ?: "",
                            onValueChange = {
                                viewModel.onAmountTextChanged(it)
                            },
                            modifier = Modifier.width(150.dp),
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                fontSize = 50.sp,
                                fontFamily = Roboto
                            ),
                            placeholder = {
                                Text(
                                    "0",
                                    modifier = Modifier.fillMaxWidth(),
                                    color = place_holder_text,
                                    fontFamily = Roboto,
                                    fontSize = 50.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = primary_blue
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(
                            modifier = Modifier.width(50.dp)
                        ) {
                            Text(
                                "sats",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontFamily = Roboto,
                                fontWeight = FontWeight.Light,
                                fontSize = 20.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp).fillMaxWidth())
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp)
                    ) {
                        OutlinedTextField(
                            value = viewModel.chatPaymentState.message,
                            onValueChange = {
                                viewModel.onMessageChanged(it)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = TextStyle(
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontFamily = Roboto
                            ),
                            placeholder = {
                                Text(
                                    "Message",
                                    modifier = Modifier.fillMaxWidth(),
                                    color = place_holder_text,
                                    fontFamily = Roboto,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                            },
                            singleLine = true,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = primary_blue
                            )
                        )
                        Divider(modifier = Modifier.fillMaxWidth(), color = light_divider)
                    }
                    Spacer(modifier = Modifier.height(40.dp).fillMaxWidth())
                    Box(
                        Modifier.fillMaxWidth().height(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.chatPaymentState.status is Response.Error) {
                            Text(
                                text = "There was an error, please try again later",
                                fontSize = 12.sp,
                                fontFamily = Roboto,
                                color = badge_red,
                            )
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
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(80.dp)
            ) {
                CommonButton(
                    callback = {
                        viewModel.sendPayment()
                    },
                    text = "Confirm",
                    enabled = viewModel.chatPaymentState.saveButtonEnabled
                )
            }
        }
    }
}
