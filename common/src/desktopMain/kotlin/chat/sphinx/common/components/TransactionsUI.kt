package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import chat.sphinx.common.Res
import chat.sphinx.common.state.TransactionState
import chat.sphinx.common.state.TransactionType
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.TransactionsViewModel
import chat.sphinx.platform.imageResource
import chat.sphinx.utils.getPreferredWindowSize
import com.example.compose.AppTheme
import kotlinx.coroutines.Delay
import kotlinx.coroutines.flow.collect
import theme.*


@Composable
fun TransactionsUI(dashboardViewModel: DashboardViewModel) {
    var isOpen by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()

    val viewModel = remember { TransactionsViewModel() }
    val viewState = viewModel.transactionViewState

    val endOfListReached by remember {
        derivedStateOf {
            listState.isScrolledToEnd()
        }
    }

    if (isOpen) {
        Window(
            onCloseRequest = { dashboardViewModel.toggleTransactionsWindow(false) },
            title = "Transactions",
            state = WindowState(
                position = WindowPosition.Aligned(Alignment.Center),
                size = getPreferredWindowSize(420, 700)
            )
        ) {
            AppTheme {
                if (viewState.loadingTransactions) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.onSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            Modifier.size(40.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
                    ) {
                        LazyColumn(
                            state = listState,
                            contentPadding = PaddingValues(top = 1.dp)
                        ) {
                            items(viewState.transactionsList) { transaction ->
                                transaction?.let {
                                    TransactionRow(transaction)
                                }
                            }
                            if (viewState.loadingMore) {
                                item { LoadingRow() }
                            }
                        }
                        VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(scrollState = listState)
                        )
                        if (endOfListReached) {
                            viewModel.loadMoreTransactions()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionRow(transaction: TransactionState) {

    val background = if (transaction.transactionType is TransactionType.Incoming) {
        MaterialTheme.colorScheme.onSecondaryContainer
    } else {
        MaterialTheme.colorScheme.background
    }

    val arrowImage = if (transaction.transactionType is TransactionType.Incoming) {
        imageResource(Res.drawable.ic_transaction_payment_received)
    } else {
        imageResource(Res.drawable.ic_transaction_payment_sent)
    }

    val iconColor = if (transaction.transactionType is TransactionType.Incoming) {
        MaterialTheme.colorScheme.inverseSurface
    } else {
        secondary_red
    }

    val textColor = if (transaction.transactionType is TransactionType.Incoming) {
        MaterialTheme.colorScheme.inverseSurface
    } else {
        wash_out_received
    }

    val dividerColor = if (transaction.transactionType is TransactionType.Incoming) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        light_divider
    }

    Box(
        modifier = Modifier.fillMaxWidth().height(80.dp).background(background),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier.width(60.dp).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = arrowImage,
                    contentDescription = "Arrow",
                    modifier = Modifier.size(12.dp),
                    colorFilter = ColorFilter.tint(color = iconColor)
                )
            }
            Box(
                modifier = Modifier.width(45.dp).fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = imageResource(Res.drawable.ic_transaction_item),
                    contentDescription = "Receipt",
                    modifier = Modifier.size(17.dp),
                    colorFilter = ColorFilter.tint(color = textColor)
                )
            }
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
                        fontWeight = FontWeight.Medium,
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
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground,
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

@Composable
fun LoadingRow() {
    Row(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            Modifier.size(25.dp),
            color = MaterialTheme.colorScheme.onBackground,
            strokeWidth = 2.dp
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Loading more...",
            fontSize = 15.sp,
            maxLines = 1,
            fontFamily = Roboto,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

fun LazyListState.isScrolledToEnd() = layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1

