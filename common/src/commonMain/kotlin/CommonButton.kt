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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType

@Composable
fun CommonButton(text:String, callback:()->Unit){
    Button(
        shape = RoundedCornerShape(23.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary),
        modifier = Modifier.fillMaxWidth(0.8f).height(48.dp),
        onClick = {
            callback()
        }
    ) {
        Text(
            text = text, fontSize = 16.sp, color = Color.White
        )
    }

}