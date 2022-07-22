package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.platform.imageResource
import chat.sphinx.wrapper.lightning.asFormattedString
import chat.sphinx.wrapper.thumbnailUrl
import chat.sphinx.wrapper.util.getInitials

@Composable
fun BoostedFooter(
    boostReactionsState: ChatMessage.BoostLayoutState
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {

        val activeIcon = boostReactionsState.boostedByOwner || boostReactionsState.showSent

        Image(
            painter = imageResource(
                if (activeIcon) Res.drawable.ic_boost_green else Res.drawable.ic_boost_gray
            ),
            contentDescription = "Boosted Icon",
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "${boostReactionsState.totalAmount.asFormattedString(' ')}",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            boostReactionsState.senders.forEachIndexed { index, it ->
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .absoluteOffset(
                            calculatePosition(boostReactionsState.senders.size, index), 0.dp
                        )
                ) {
                    if (index < 3) {
                        PhotoUrlImage(
                            photoUrl = it.photoUrl?.thumbnailUrl,
                            modifier = Modifier
                                .size(25.dp)
                                .clip(CircleShape),
                            color = if (it.color != null) Color(it.color) else Color.Gray,
                            firstNameLetter = (it.alias?.value)?.getInitials(),
                            fontSize = 9
                        )
                    }
                }
            }
        }
        if (boostReactionsState.senders.size > 3) {
            Text(
                text = "+${(boostReactionsState.senders.size) - 3}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

fun calculatePosition(size: Int, index: Int): Dp {
    when (size) {
        1 ->  when(index){
            0->{
                return 0.dp
            }
        }
        2 -> when(index){
            0->{
                return -(12.5.dp)
            }
            1->{
                return 0.dp
            }
        }
        else -> when(index){
            0->{
                return -(25.dp)
            }
            1->{
                return -(12.dp)
            }
            2->{
                return 0.dp
            }
        }
    }
    return 0.dp
}
