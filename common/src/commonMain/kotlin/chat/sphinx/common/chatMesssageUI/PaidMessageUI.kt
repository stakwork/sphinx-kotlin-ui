package chat.sphinx.common.chatMesssageUI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.models.ChatMessage
import chat.sphinx.platform.imageResource
import chat.sphinx.wrapper.message.PurchaseStatus
import chat.sphinx.wrapper.message.retrievePaidTextAttachmentUrlAndMessageMedia
import chat.sphinx.wrapper.message.retrievePurchaseStatus

@Composable
fun PaidMessageUI(chatMessage: ChatMessage){
    chatMessage.message.retrievePurchaseStatus()?.run {

        when(this){
            PurchaseStatus.Accepted -> "LOADING MESSAGE..."
            PurchaseStatus.Denied -> "UNABLE TO LOAD MESSAGE DATA"
            PurchaseStatus.Pending -> "PAY TO UNLOCK MESSAGE"
            PurchaseStatus.Processing -> "LOADING MESSAGE..."
        }
//        if(this is PurchaseStatus.Accepted)
//            chatMessage.message.retrievePaidTextAttachmentUrlAndMessageMedia()
    }?.let {
        val amount=chatMessage.message.messageMedia?.price?.value.toString()
       if(chatMessage.isReceived)
           ReceivedPaidMessage(it,amount, chatMessage.message.retrievePurchaseStatus()!!)
        else Column {
           SentPaidMessage(amount,chatMessage.message.retrievePurchaseStatus()!!)
           Spacer(modifier = Modifier.height(8.dp))
           Text("Decrypted Message", modifier = Modifier.padding(4.dp), color = MaterialTheme.colorScheme.tertiary, fontSize = 13.sp)
       }
    }

}

@Composable
private fun ReceivedPaidMessage(message: String,amount: String,status:PurchaseStatus){
    Column (modifier = Modifier.fillMaxWidth(0.5f)){
        Text(message, fontWeight = FontWeight.W600, fontSize =  if((status is PurchaseStatus.Accepted).not()) 10.sp else 12.sp, color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(16.dp))
        Spacer(modifier = Modifier.height(8.dp))
        if((status is PurchaseStatus.Accepted).not())
        SendSats(amount)
        else ShowPaidMessage(amount)
    }

}

@Composable
fun SendSats(amount: String) {
    Row(modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer).fillMaxWidth().height(40.dp), verticalAlignment = Alignment.CenterVertically,) {
//            Spacer(modifier = Modifier.width(2.dp).height(2.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Image(
            painter = imageResource(Res.drawable.ic_sent), contentDescription = null, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("PAY", color = MaterialTheme.colorScheme.tertiary, fontSize = 14.sp, fontWeight = FontWeight.W600, modifier = Modifier.weight(1f))
        Text("$amount SAT", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
        Spacer(modifier = Modifier.width(8.dp).height(8.dp))
    }
}

@Composable
fun ShowPaidMessage(amount: String) {
    Row(modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer).fillMaxWidth().height(50.dp), verticalAlignment = Alignment.CenterVertically,) {
//            Spacer(modifier = Modifier.width(2.dp).height(2.dp))
        Spacer(modifier = Modifier.width(8.dp))
//        Image(
//            painter = imageResource(Res.drawable.ic_done), contentDescription = null, colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.tertiary)
//        )
        Icon(Icons.Default.Done, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Purchase Succeed", color = MaterialTheme.colorScheme.tertiary, fontSize = 12.sp, fontWeight = FontWeight.W600, modifier = Modifier.weight(1f))
    }
}
@Composable
private fun SentPaidMessage(amount:String,status: PurchaseStatus){
    val text= when(status){
        PurchaseStatus.Accepted -> "Purchase Success"
        PurchaseStatus.Denied -> "Purchase Failed"
        PurchaseStatus.Pending -> "Pending"
        PurchaseStatus.Processing -> "Processing"
    }
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(8.dp)) {
        Card ( backgroundColor = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp),
        ){
            Text(amount,fontSize = 10.sp, color = MaterialTheme.colorScheme.tertiary,modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Card ( backgroundColor = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(4.dp),
        ){

            Text(text, fontWeight = FontWeight.W600, fontSize = 10.sp, color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
        }

    }
}