package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.unit.sp

@Composable
fun MnemonicDialog(
    mnemonicWords: String,
    onDismiss: () -> Unit,
    onCopyToClipboard: () -> Unit
) {
    val clipboardManager = LocalClipboardManager.current

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)  // Makes the background transparent
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
                    .background(
                        color = androidx.compose.material3.MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "Your Mnemonic Words",
                    textAlign = TextAlign.Center,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W500,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    fontFamily = Roboto
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = mnemonicWords,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    fontSize = 13.sp,
                    fontFamily = Roboto,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(mnemonicWords))
                            onCopyToClipboard()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            "Copy",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                            fontFamily = Roboto
                        )
                    }
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            "Close",
                            color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                            fontFamily = Roboto
                        )
                    }
                }
            }
        }
    }
}
