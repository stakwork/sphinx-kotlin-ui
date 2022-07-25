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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.state.LandingScreenState
import chat.sphinx.common.state.LandingScreenType

@Composable
fun CommonButton(
    text: String,
    enabled: Boolean? = true,
    backgroundColor: Color? = null,
    callback:()->Unit
) {
    val color= if (enabled == true) {
        backgroundColor
            ?: androidx.compose.material3.MaterialTheme.colorScheme.secondary
    } else {
        backgroundColor?.copy(alpha = 0.7f)
            ?: androidx.compose.material3.MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
    }

    val textColor= if (enabled == true) androidx.compose.material3.MaterialTheme.colorScheme.tertiary else androidx.compose.material3.MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)

    Button(
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = color),
        modifier = Modifier.fillMaxWidth().height(60.dp),
        onClick = {
            if (enabled==true) callback()
        }
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = textColor,
            fontWeight = FontWeight.W400,
            fontFamily = Roboto
        )
    }

}