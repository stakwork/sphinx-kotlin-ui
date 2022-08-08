package chat.sphinx.common.components.tribe

import CommonButton
import Roboto
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.components.PhotoUrlImage
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.utils.getPreferredWindowSize
import chat.sphinx.wrapper.PhotoUrl

@Composable
actual fun JoinTribeView(dashboardViewModel: DashboardViewModel) {
    Window(
        onCloseRequest = {
            dashboardViewModel.toggleJoinTribeWindow(false)
        },
        title = "Sphinx",

        state = WindowState(
            position = WindowPosition.Aligned(Alignment.Center),
            size = getPreferredWindowSize(400, 800)
        ),
//        icon = sphinxIcon,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onSurfaceVariant).padding(vertical = 24.dp, horizontal = 16.dp).verticalScroll(
                rememberScrollState()
            ), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PhotoUrlImage(
                PhotoUrl("https://picsum.photos/200/300"), modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("SLC Bitcoin Lightening Meet Up", fontSize = 28.sp, color = MaterialTheme.colorScheme.tertiary, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(24.dp))
            Text("Salt lake city meetup first thursday of the month", color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center, fontSize = 15.sp, modifier = Modifier.padding(horizontal = 4.dp))
            Spacer(modifier = Modifier.height(24.dp))
           Column(modifier = Modifier.padding(horizontal = 24.dp)){
               BoxWithStroke("Price to join:","1",BoxWithStrokeEnums.TOP)
               BoxWithStroke("Price per message:","2")
               BoxWithStroke("Amount to stake:","5")
               BoxWithStroke("Time to stake: (hours)","2",BoxWithStrokeEnums.BOTTOM)
               Spacer(modifier = Modifier.height(24.dp))
               TribeTextField("Alias", "Sachin") {}
               Box (){
                   TribeTextField("Profile Picture", "https://www.google.co.in") {}
                   Box(modifier = Modifier.align(Alignment.TopEnd).padding(end = 16.dp)){
                       PhotoUrlImage(
                           PhotoUrl("https://picsum.photos/200/300"), modifier = Modifier
                               .size(40.dp)
                               .clip(CircleShape).align(Alignment.TopEnd)
                       )
                   }
               }
               Spacer(modifier = Modifier.height(32.dp))
               Button(
                   shape = RoundedCornerShape(30.dp),
                   colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colorScheme.secondary),
                   modifier = Modifier.fillMaxWidth(0.7f).height(45.dp).align(Alignment.CenterHorizontally),
                   onClick = {

                   }
               ) {
                   androidx.compose.material.Text(
                       text = "JOIN TRIBE",
                       fontSize = 14.sp,
                       color = MaterialTheme.colorScheme.tertiary,
                       fontWeight = FontWeight.W800,
                       fontFamily = Roboto
                   )
               }
           }
        }


    }
}

@Composable
fun BoxWithStroke(labelName:String,value:String,boxWithStrokeEnums: BoxWithStrokeEnums=BoxWithStrokeEnums.NONE) {
    val roundedCornerShape=when(boxWithStrokeEnums){
        BoxWithStrokeEnums.TOP -> RoundedCornerShape(topEnd = 4.dp, topStart = 4.dp)
        BoxWithStrokeEnums.BOTTOM -> RoundedCornerShape(bottomEnd = 4.dp, bottomStart = 4.dp)
        BoxWithStrokeEnums.NONE -> RoundedCornerShape(0.dp)
    }
    Card (border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.onBackground), shape = roundedCornerShape, backgroundColor = MaterialTheme.colorScheme.onSurfaceVariant){
        Row (modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Text(labelName, color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
            Text(value, color = MaterialTheme.colorScheme.onBackground, fontSize = 13.sp)
        }
    }
}
 enum class BoxWithStrokeEnums{
    TOP,
    BOTTOM,
    NONE
}
