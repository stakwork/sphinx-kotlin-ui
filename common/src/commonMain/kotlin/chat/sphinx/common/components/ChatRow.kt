package chat.sphinx.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.models.DashboardChat
import chat.sphinx.common.state.ChatDetailData
import chat.sphinx.common.state.ChatDetailState
import chat.sphinx.wrapper.DateTime
import chat.sphinx.wrapper.chat.ChatMuted


@OptIn(ExperimentalStdlibApi::class)
@Composable
fun ChatRow(
    dashboardChat: DashboardChat
) {
    // TODO: Create DashboardChatState...
    val today00: DateTime by lazy {
        DateTime.getToday00()
    }

    Row(
        modifier = Modifier.clickable {
            ChatDetailState.screenState(

                when (dashboardChat) {

                    is DashboardChat.Active.Conversation -> {
                        ChatDetailData.SelectedChatDetailData.SelectedContactChatDetail(
                            dashboardChat.chat.id,
                            dashboardChat.contact.id,
                            dashboardChat
                        )
                    }
                    is DashboardChat.Active.GroupOrTribe -> {
                        ChatDetailData.SelectedChatDetailData.SelectedTribeChatDetail(
                            dashboardChat.chat.id,
                            dashboardChat
                        )
                    }
                    else -> ChatDetailData.EmptyChatDetailData
                }
            )

        }.padding(12.dp),
    ) {
//        AsyncImage(
//            model = "https://example.com/image.jpg",
//            contentDescription = null
//        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .aspectRatio(1f, true)
                .background(SolidColor(Color.Blue), alpha = 0.50f)
        ) {
            Text(
                text = dashboardChat.chatName?.take(1) ?: " "
            )
        }

      Row{
          PhotoUrlImage(
              dashboardChat.photoUrl,
              modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)                       // clip to the circle shape
                  .border(2.dp, Color.Gray, CircleShape)   // add a border (optional)
          )

          Spacer(modifier = Modifier.width(6.dp))
          Column {

              Row(verticalAlignment = Alignment.CenterVertically) {


                  Text(
                      text = dashboardChat.chatName ?: "Unknown Chat",
                      fontSize = 15.sp,
                      maxLines = 1,
                      color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                      overflow = TextOverflow.Ellipsis
                  )
                  // TODO: Muted icon...
                  Spacer(modifier = Modifier.width(4.dp))
                  if(dashboardChat.isEncrypted())
                  Icon(
                      Icons.Filled.Lock,
                      contentDescription = null,
                      modifier = Modifier.size(14.dp),
                      tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                  )
                  Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                      Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                          if(dashboardChat is DashboardChat.Active){
                              if(dashboardChat.chat.isMuted.value== ChatMuted.MUTED)
                                  Icon(
                                      Icons.Filled.NotificationsOff,
                                      contentDescription = null,
                                      modifier = Modifier.size(14.dp),
                                      tint = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                                  )
                          }

                          Spacer(modifier = Modifier.width(2.dp))
                          Text(
                              text = dashboardChat.getDisplayTime(today00),
                              fontSize = 10.sp,
                              maxLines = 1,
                              color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,

                              )

                      }

                  }
              }
              Spacer(modifier = Modifier.height(4.dp))

              Row (modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,){
                  Text(
                      text = dashboardChat.getMessageText(),
                      fontSize = 13.sp,
                      fontWeight = if (dashboardChat.hasUnseenMessages()) FontWeight.W400 else FontWeight.W700,
                      maxLines = 1,
                      overflow = TextOverflow.Ellipsis,
                      modifier = Modifier.weight(1f),
                      color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground,
                  )

                dashboardChat.unseenMessageFlow?.collectAsState(0)?.let {
                    if(it.value!=0L)
                    MessageCount(it.value.toString())
                }

//                }


              }


          }
      }

    }
}

@Composable
fun MessageCount(messageCount:String){
    Box(contentAlignment= Alignment.Center,
        modifier = Modifier
            .background( color=androidx.compose.material3.MaterialTheme.colorScheme.secondary, shape = RoundedCornerShape(16.dp))
//            .layout(){ measurable, constraints ->
//                // Measure the composable
//                val placeable = measurable.measure(constraints)
//
//                //get the current max dimension to assign width=height
//                val currentHeight = placeable.height
//                var heightCircle = currentHeight
//                if (placeable.width > heightCircle)
//                    heightCircle = placeable.width
//
//                //assign the dimension and the center position
//                layout(heightCircle, heightCircle) {
//                    // Where the composable gets placed
//                    placeable.placeRelative(0, (heightCircle-currentHeight)/2)
//                }
//            }
            ) {

        Text(
            text = messageCount,
            textAlign = TextAlign.Center,
            color = Color.White,
            modifier = Modifier.padding(4.dp).defaultMinSize(24.dp),
            fontSize = 11.sp
            //Use a min size for short text.
        )
    }
}