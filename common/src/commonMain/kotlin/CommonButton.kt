import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType

@Composable
fun CommonButton(
    text: String,
    enabled: Boolean? = true,
    customColor: Color?=null,
    textButtonSize:TextUnit=16.sp,
    callback:()->Unit
) {
    val color=if(enabled == true)androidx.compose.material3.MaterialTheme.colorScheme.secondary else androidx.compose.material3.MaterialTheme.colorScheme.onBackground
    val textColor=if(enabled == true)androidx.compose.material3.MaterialTheme.colorScheme.tertiary else Color.Black
    Button(
        shape = RoundedCornerShape(23.dp),
//        enabled = enabled?:true,
        colors = ButtonDefaults.buttonColors(backgroundColor = customColor ?: color),
        modifier = Modifier.fillMaxWidth().height(48.dp),
        onClick = {
            if(enabled==true)
            callback()
        }
    ) {
        Text(
            text = text,
            fontSize = textButtonSize,
            color = textColor,
            fontWeight = FontWeight.W400,
            fontFamily = Roboto
        )
    }

}