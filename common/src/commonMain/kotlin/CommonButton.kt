import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CommonButton(
    text: String,
    enabled: Boolean? = true,
    customColor: Color? = null,
    endIcon: ImageVector? = null,
    textButtonSize:TextUnit = 16.sp,
    backgroundColor: Color? = null,
    fontWeight: FontWeight = FontWeight.W400,
    callback:()->Unit
) {
    val color= if (enabled == true) {
        backgroundColor ?: androidx.compose.material3.MaterialTheme.colorScheme.secondary
    } else {
        backgroundColor?.copy(alpha = 0.7f) ?: androidx.compose.material3.MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f)
    }

    val textColor= if (enabled == true) {
        androidx.compose.material3.MaterialTheme.colorScheme.tertiary
    } else {
        androidx.compose.material3.MaterialTheme.colorScheme.tertiary.copy(alpha = 0.7f)
    }

    Button(
        shape = RoundedCornerShape(23.dp),
//        enabled = enabled?:true,
        colors = ButtonDefaults.buttonColors(backgroundColor = customColor ?: color),
        modifier = Modifier.fillMaxWidth().height(48.dp),
        onClick = {
            if (enabled==true) callback()
        }
    ) {
        Box {
            Text(
                text = text,
                fontSize = textButtonSize,
                color = textColor,
                fontWeight = fontWeight,
                fontFamily = Roboto,
                modifier = Modifier.fillMaxWidth().align(Alignment.Center),
                textAlign = TextAlign.Center
            )
            if(endIcon != null) {
                Icon(
                    imageVector = endIcon,
                    contentDescription = "",
                    tint = Color.White,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }

}