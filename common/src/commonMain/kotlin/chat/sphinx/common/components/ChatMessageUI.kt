package chat.sphinx.common.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.common.state.EditMessageState
import chat.sphinx.common.viewmodel.chat.ChatViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.linkify.LinkTag
import chat.sphinx.utils.toAnnotatedString
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.isTribe
import chat.sphinx.wrapper.chatTimeFormat
import chat.sphinx.wrapper.message.*
import chat.sphinx.wrapper.message.media.isImage
import kotlinx.coroutines.launch
import views.LoadingShimmerEffect
import views.ShimmerCircleAvatar


@Composable
fun ChatMessageUI(
    chatMessage: ChatMessage,
    editMessageState: EditMessageState,
    chatViewModel: ChatViewModel, color: Color
) {


    Column(modifier = Modifier.padding(8.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = if (chatMessage.isSent) Arrangement.End else Arrangement.Start
        ) {
            Row(
                verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth(0.8f),
            ) {
                if (chatMessage.isReceived) {
                    if (chatMessage.message.senderPic != null && chatMessage.message.senderPic.toString() != "null")

                        ImageProfile(chatMessage, color) // User Image Profile
                    else {
                        ImageProfile(chatMessage, color)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    if (chatMessage.message.type.isGroupAction()) {
                        // If any joined tribe will show below text
                        chatMessage.groupActionLabelText?.let { groupActionLabelText ->
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = groupActionLabelText,
                                fontWeight = FontWeight.W300,
                                textAlign = TextAlign.Center,
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            horizontalArrangement = if (chatMessage.isSent) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DisplayConditionalIcons(
                                chatMessage,
                                chatViewModel,
                                editMessageState, color
                            ) // display icons according to different conditions
                        }
                        ChatCard(chatMessage, color)

                    }

                }
                if (chatMessage.isReceived.not()) {
                    Spacer(modifier = Modifier.width(12.dp))
                    val coroutineScope = rememberCoroutineScope()
                    val url = remember { mutableStateOf<PhotoUrl?>(null) }
                    coroutineScope.launch {
                        url.value = chatViewModel.getOwner().photoUrl

                    }
                    ImageProfile(url.value, color)
//                    Box(modifier = Modifier.size(35.dp)) {
//                        var url:PhotoUrl?=null
//                        LaunchedEffect(key1="1"){
//                            url = chatViewModel.getOwner().photoUrl
//
//                        }
//
//
//                    }
                }


            }
        }

    }
}

@Composable
fun DisplayConditionalIcons(
    chatMessage: ChatMessage,
    chatViewModel: ChatViewModel,
    editMessageState: EditMessageState, color: Color
) {
    val isMessageMenuVisible = mutableStateOf(false)
    if (chatMessage.isSent) {
//        TODO fix the option menu spacing issue
//        KebabMenu(
//            contentDescription = "Menu for message",
//            onClick = { isMessageMenuVisible.value = true }
//        )
    }

    if (chatMessage.chat.isTribe()) {
        Text(
            text = chatMessage.message.senderAlias?.value ?: "",
            color = color, fontSize = 10.sp
        )
        Spacer(
            modifier = Modifier.width(4.dp)
        )
    }

    if (chatMessage.showSendingIcon) {
        CircularProgressIndicator(
            modifier = Modifier.size(22.dp)
        )
    }

    if (chatMessage.showBoltIcon) {
        Icon(
            Icons.Default.FlashOn,
            "Confirmed",
            tint = Color.Green,
            modifier = Modifier.size(18.dp).padding(4.dp)
        )
    }


    Text(
        text = chatMessage.message.date.chatTimeFormat(),
        fontWeight = FontWeight.W200,
        color = Color(0xFF556171),
        fontSize = 10.sp,
        textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
    )


    if (chatMessage.showLockIcon) {
        Icon(
            Icons.Default.Lock,
            "Secure chat",
            tint = Color(0xFF556171),
            modifier = Modifier.size(18.dp).padding(4.dp)
        )
    }
    // TODO fix the spacing issue
//    MessageMenu(
//        chatMessage = chatMessage,
//        editMessageState = editMessageState,
//        isVisible = isMessageMenuVisible,
//        chatViewModel
//    )
}

@Composable
fun ChatCard(chatMessage: ChatMessage, color: Color) {
    val uriHandler = LocalUriHandler.current
    val receiverCorner =
        RoundedCornerShape(topEnd = 10.dp, topStart = 0.dp, bottomEnd = 10.dp, bottomStart = 10.dp)
    val senderCorner =
        RoundedCornerShape(topEnd = 0.dp, topStart = 10.dp, bottomEnd = 10.dp, bottomStart = 10.dp)
    Card(
        backgroundColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = if (chatMessage.isReceived) receiverCorner else senderCorner
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            Column {


                chatMessage.message.replyMessage?.let { replyMessage ->
                    SenderNameWithTime(replyMessage, color)
                    Spacer(modifier = Modifier.height(4.dp))
                    // TODO: Might want a divider here....
                }
                chatMessage.message.messageMedia?.let { media ->
                    // TODO: Show attachment
                    if (media.mediaType.isImage) {
                        //                        val mediaData = chatMessage.message.retrieveUrlAndMessageMedia()
                        Icon(
                            Icons.Default.Image,
                            contentDescription = "Image",
                            tint = Color.Green,
                            modifier = Modifier.size(88.dp).padding(4.dp)
                        )
                    } else {
                        // show
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = "Attachment",
                            tint = Color.Green,
                            modifier = Modifier.size(88.dp).padding(4.dp)
                        )
                    }
                }

                if (chatMessage.isDeleted) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "This message has been deleted",
                        fontWeight = FontWeight.W300,
                        textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
                    )
                } else if (chatMessage.isFlagged) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "This message has been flagged",
                        fontWeight = FontWeight.W300,
                        textAlign = if (chatMessage.isSent) TextAlign.End else TextAlign.Start,
                    )
                } else {
                    chatMessage.message.retrieveTextToShow()?.let { messageText ->
                        Row(
                            modifier = Modifier.fillMaxWidth(0.9f),
//                            horizontalArrangement = if (chatMessage.isSent) Arrangement.End else Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val annotatedString = messageText.toAnnotatedString()
                            ClickableText(
                                annotatedString,
                                style = TextStyle(
                                    fontWeight = FontWeight.W400,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontSize = 13.sp
                                ),
                                onClick = { offset ->
                                    annotatedString.getStringAnnotations(
                                        start = offset,
                                        end = offset
                                    ).firstOrNull()?.let { annotation ->
                                        when (annotation.tag) {
                                            LinkTag.WebURL.name -> {
                                                uriHandler.openUri(annotation.item)
                                            }
                                            LinkTag.BitcoinAddress.name -> {
                                                val bitcoinUriScheme =
                                                    if (annotation.item.startsWith("bitcoin:")) "bitcoin:" else ""
                                                val bitcoinURI =
                                                    "$bitcoinUriScheme${annotation.item}"

                                                uriHandler.openUri(bitcoinURI)
                                            }
                                        }
                                    }
                                }
                            )

                            // TODO: Make clickable text compatible with selectable text...
                            //                                SelectionContainer {
                            //
                            //                                }
                        }

                    }
                    if (chatMessage.message.type == MessageType.BotRes) {
                        chatMessage.message.messageContentDecrypted?.let {
                            val annotatedString = it.value.toAnnotatedString()
                            ClickableText(
                                annotatedString,
                                style = TextStyle(
                                    fontWeight = FontWeight.W400,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontSize = 13.sp
                                ),
                                onClick = { offset ->
                                    annotatedString.getStringAnnotations(
                                        start = offset,
                                        end = offset
                                    ).firstOrNull()?.let { annotation ->
                                        when (annotation.tag) {
                                            LinkTag.WebURL.name -> {
                                                uriHandler.openUri(annotation.item)
                                            }
                                            LinkTag.BitcoinAddress.name -> {
                                                val bitcoinUriScheme =
                                                    if (annotation.item.startsWith("bitcoin:")) "bitcoin:" else ""
                                                val bitcoinURI =
                                                    "$bitcoinUriScheme${annotation.item}"

                                                uriHandler.openUri(bitcoinURI)
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }

                if (chatMessage.showFailedContainer) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = "Go back",
                            tint = Color.Red,
                            modifier = Modifier.size(22.dp).padding(4.dp)
                        )
                        Text(
                            text = "Failed message",
                            color = Color.Red,
                            textAlign = TextAlign.Start
                        )
                    }

                }
                if (chatMessage.message.reactions?.isNotEmpty() == true) {
                    Spacer(modifier = Modifier.height(4.dp))
                    BoostedFooter(chatMessage)
                }
                // TODO: Attachment not supported... but give download functionality...
            }
        }
    }
}

@Composable
fun BoostedFooter(chatMessage: ChatMessage) {
    val reaction = chatMessage.message.reactions?.get(0)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = imageResource(Res.drawable.ic_boost_green),
            contentDescription = "Boosted Icon",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${reaction?.amount?.value} sats",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Spacer(modifier = Modifier.fillMaxWidth(0.8f))
        reaction?.senderPic?.let {
            PhotoUrlImage(
                photoUrl = it,
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)                       // clip to the circle shape
                    .border(2.dp, Color.Gray, CircleShape),
                effect = {
                    Box(
                        modifier = androidx.compose.ui.Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                    ) {
                        LoadingShimmerEffect {
                            ShimmerCircleAvatar(it)
                        }
                    }
                },
            )
        }

    }
}

@Composable
fun SenderNameWithTime(replyMessage: Message, color: Color) {
    Row(
        modifier = Modifier.height(44.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(color)
                .padding(16.dp),
        )
        // TODO: Image if available...
        replyMessage.messageMedia?.let { media ->
            if (media.mediaType.isImage) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = "Image",
                    tint = Color.Green,
                    modifier = Modifier.size(88.dp).padding(4.dp)
                )
            } else {
                // show
                Icon(
                    Icons.Default.AttachFile,
                    contentDescription = "Attachment",
                    tint = Color.Green,
                    modifier = Modifier.size(88.dp).padding(4.dp)
                )
            }
        }
        Column(
            modifier = Modifier.padding(
                start = 8.dp
            ),
            verticalArrangement = Arrangement.Center
        ) {
            replyMessage.senderAlias?.let { senderAlias ->
                Text(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    text = senderAlias.value.trim(),
                    fontWeight = FontWeight.W300,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    overflow = TextOverflow.Ellipsis
                )
            }
            replyMessage.retrieveTextToShow()?.let { replyMessageText ->
                if (replyMessageText.isNotEmpty()) {
                    Text(
                        modifier = Modifier.fillMaxWidth(0.8f),
                        text = replyMessageText,
                        fontWeight = FontWeight.W300,
                        textAlign = TextAlign.Start,
                        maxLines = 1,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun ImageProfile(chatMessage: ChatMessage, color: Color) {
    PhotoUrlImage(
        chatMessage.message.senderPic,
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)                       // clip to the circle shape
            .border(2.dp, Color.Gray, CircleShape), effect = {
            Box(
                modifier = androidx.compose.ui.Modifier
                    .size(30.dp)
                    .clip(CircleShape)
            ) {
                LoadingShimmerEffect {
                    ShimmerCircleAvatar(it)
                }
            }
        },
        color = color, firstNameLetter = chatMessage.message.senderAlias?.value?.split("")?.get(1)
        // add a border (optional)
    )
}

@Composable
fun ImageProfile(profileURL: PhotoUrl?, color: Color) {
    PhotoUrlImage(
        profileURL,
        modifier = Modifier
            .size(30.dp)
            .clip(CircleShape)                       // clip to the circle shape
            .border(2.dp, Color.Gray, CircleShape), effect = {
            Box(
                modifier = androidx.compose.ui.Modifier
                    .size(30.dp)
                    .clip(CircleShape)
            ) {
                LoadingShimmerEffect {
                    ShimmerCircleAvatar(it)
                }
            }
        },
        color = color
        // add a border (optional)
    )
}

@Composable
fun ChatMessageUIPlaceholder() {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(
                vertical = 30.dp
            )
    ) {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth()
        )
    }
}