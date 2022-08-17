package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NorthEast
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
import chat.sphinx.common.state.TransactionState
import chat.sphinx.common.state.TransactionType
import chat.sphinx.common.viewmodel.TransactionsViewModel
import chat.sphinx.utils.getPreferredWindowSize
import com.example.compose.AppTheme
import kotlinx.coroutines.Delay
import theme.primary_blue
import theme.primary_red
import kotlinx.coroutines.flow.collect


@Composable
fun TransactionsUI() {
    var isOpen by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    val viewModel = remember { TransactionsViewModel()}
    val transactionsList = viewModel.transactionsListStateFlow.collectAsState().value

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
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(top = 1.dp)
                    ) {
                        items(transactionsList) { transaction ->
                            TransactionRow(transaction)
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState = listState)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionRow(transaction: TransactionState) {

    val background = if(transaction.transactionType is TransactionType.Incoming){
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.background
    }

    val imageVector = if(transaction.transactionType is TransactionType.Incoming) {
        Icons.Default.NorthEast
    } else {
        Icons.Default.SouthWest
    }

    val iconColor = if(transaction.transactionType is TransactionType.Incoming) {
        primary_blue
    } else {
        primary_red
    }

    val textColor = if(transaction.transactionType is TransactionType.Incoming) {
        primary_blue
    } else {
        Color.Gray
    }

    val dividerColor = if(transaction.transactionType is TransactionType.Incoming) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        Color.DarkGray
    }


    Box(
        modifier = Modifier.fillMaxWidth().height(80.dp).background(background),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = "Outgoing",
                tint = iconColor,
                modifier = Modifier.padding(start = 18.dp),
            )
            Spacer(modifier = Modifier.width(32.dp))

            Icon(
                Icons.Default.Receipt,
                contentDescription = "Receipt",
                tint = textColor,
            )
            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = transaction.senderReceiverName,
                maxLines = 1,
                fontSize = 14.sp,
                fontFamily = Roboto,
                color = textColor,
            )

            Column(
                modifier = Modifier.fillMaxSize().padding(top = 16.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Row() {
                    Text(
                        text = transaction.amount,
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
                        color = textColor,
                        modifier = Modifier.padding(start = 6.dp, end = 12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = transaction.date,
                    fontSize = 10.sp,
                    maxLines = 1,
                    fontFamily = Roboto,
                    color = Color.LightGray,
                    modifier = Modifier.padding(end = 12.dp)

                )
            }
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Divider(modifier = Modifier.fillMaxWidth(), color = dividerColor)
        }
    }
}

//fun LazyListState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

