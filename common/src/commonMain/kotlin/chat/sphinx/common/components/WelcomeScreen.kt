package chat.sphinx.common.components

import CommonButton
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.state.*
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.SphinxFonts

@Composable
fun WelcomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,

        ) {

        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

            Image(
                painter = imageResource(Res.drawable.ic_done), contentDescription = "connecting",
                modifier = Modifier.width(120.dp)
                    .height(68.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(38.dp))
            Text(
                text = "WELCOME",
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.W500,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontFamily = SphinxFonts.montserratFamily
                // textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your app is now connected",
                fontSize = 22.sp,
                color = Color.Gray,
                fontWeight = FontWeight.W400,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                // textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(38.dp))
            Row(
                modifier = Modifier.fillMaxWidth(0.5f), horizontalArrangement = Arrangement.Center){
                Box(
                    modifier = Modifier.height(44.dp).fillMaxWidth(0.7f), contentAlignment = Alignment.Center){
                    CommonButton(text = "Continue",true){
                        DashboardScreenState.screenState(DashboardScreenType.Unlocked)
                        AppState.screenState(ScreenType.DashboardScreen)
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize().padding(16.dp, 0.dp)
                    ) {
                        Icon(
                            Icons.Filled.ArrowForward,
                            "arrow",
                            modifier = Modifier.size(18.dp),
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }
        }
    }
}