package chat.sphinx.common.components.landing

import CommonButton
import Roboto
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.Res
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType
import chat.sphinx.platform.imageResource
import theme.md_theme_dark_onBackground

@Composable
fun OnBoardSphinxOnYourPhone() {
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.secondary),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 86.dp)
        ) {
            Text(
                text = "Don't have ",
                fontSize = 30.sp,
                maxLines = 1,
                color = Color.White,
                fontFamily = Roboto,
                fontWeight = FontWeight.ExtraLight,
            )
            Text(
                text = "Sphinx on your phone? ",
                fontSize = 30.sp,
                maxLines = 1,
                color = Color.White,
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
            )
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().height(460.dp).padding(end = 84.dp)
        ) {
            Image(
                painter = imageResource(Res.drawable.ending_page_image),
                contentDescription = "Sphinx ending page graphic",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ){
            Box(modifier = Modifier.height(48.dp).width(259.dp)) {
                Button(text = "Get it ", backgroundColor = MaterialTheme.colorScheme.secondary)
            }

            Spacer(modifier = Modifier.width(84.dp))

            Box(modifier = Modifier.height(48.dp).width(259.dp)) {
                Button(text = "Skip ", backgroundColor = Color.White)

            }
        }

    }
}

@Composable
private fun Button(text:String, backgroundColor: Color){
    Card(
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(23.dp),
        border = BorderStroke(1.dp, Color.White)
    ) {
        CommonButton(
            text = text,
            enabled = true,
            backgroundColor = backgroundColor,
        ) {}
    }
}