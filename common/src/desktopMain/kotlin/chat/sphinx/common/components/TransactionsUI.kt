package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.utils.getPreferredWindowSize
import com.example.compose.AppTheme

@Composable
fun TransactionsUI() {
    var isOpen by remember { mutableStateOf(true) }
    if (isOpen) {
        Window(
            onCloseRequest = {},
            title = "Transactions",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(420, 620)
            )
        ) {
            AppTheme {
                Box(
                    modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                ) {
                    TransactionRow()
                }
            }
        }
    }
}

@Composable
fun TransactionRow() {
    Box(
        modifier = Modifier.fillMaxWidth().height(80.dp).background(MaterialTheme.colorScheme.onSecondaryContainer),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                Icons.Default.SouthWest,
                contentDescription = "Outgoing",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(start = 18.dp),
            )
            Spacer(modifier = Modifier.width(32.dp))

            Icon(
                Icons.Default.Receipt,
                contentDescription = "Receipt",
                tint = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Ricky",
                maxLines = 1,
                fontSize = 14.sp,
                fontFamily = Roboto,
                color = MaterialTheme.colorScheme.secondary,
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(top = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Row() {
                    Text(
                        text = "1000",
                        fontSize = 14.sp,
                        maxLines = 1,
                        fontFamily = Roboto,
                        color = Color.White,
                    )
                    Text(
                        text = "sat",
                        maxLines = 1,
                        fontSize = 14.sp,
                        fontFamily = Roboto,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(start = 6.dp, end = 12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "Tue Aug 16, 1:33 PM",
                    fontSize = 10.sp,
                    maxLines = 1,
                    fontFamily = Roboto,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 12.dp)

                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Divider(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}