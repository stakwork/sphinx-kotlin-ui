package chat.sphinx.common.components

import Roboto
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun TransactionRow(transaction: TransactionState) {

    val background = when (transaction.transactionType) {
        is TransactionType.Incoming -> {
            incoming_pmt_background
        }

        is TransactionType.Outgoing -> {
            outgoing_pmt_background
        }

        else -> {
            failed_pmt_background
        }
    }

    val arrowImage = when (transaction.transactionType) {
        is TransactionType.Incoming -> {
            imageResource(Res.drawable.ic_received)
        }

        is TransactionType.Outgoing -> {
            imageResource(Res.drawable.ic_sent)
        }

        else -> {
            imageResource(Res.drawable.ic_warning)
        }
    }

    val iconColor = when (transaction.transactionType) {
        is TransactionType.Incoming -> {
            primary_green
        }

        is TransactionType.Outgoing -> {
            randomColor4
        }

        else -> {
            secondary_red
        }
    }

    val textColor = if (transaction.transactionType is TransactionType.Incoming) {
        wash_out_received
    } else {
        wash_out_received
    }

    val dividerColor = if (transaction.transactionType is TransactionType.Incoming) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        light_divider
    }

    var showErrorMessage by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.clickable { showErrorMessage = !showErrorMessage }
            .background(background)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Box(
                    modifier = Modifier.width(60.dp).fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    if (transaction.transactionType is TransactionType.Failed) {
                        Image(
                            painter = arrowImage,
                            contentDescription = "Arrow",
                            modifier = Modifier.requiredSize(20.dp),
                            colorFilter = ColorFilter.tint(color = iconColor)
                        )
                    } else {
                        Image(
                            painter = arrowImage,
                            contentDescription = "Warning",
                            modifier = Modifier.size(30.dp),
                            colorFilter = ColorFilter.tint(color = iconColor)

                        )
                    }
                }
                Column {
                    Text(
                        text = transaction.senderReceiverName,
                        maxLines = 1,
                        fontSize = 14.sp,
                        fontFamily = Roboto,
                        color = sphinx_action_menu,
                    )

                    if (transaction.transactionType is TransactionType.Failed) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Failed Payment",
                            fontSize = 13.sp,
                            fontFamily = Roboto,
                            color = primary_red,
                        )
                    }
                }
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
                            color = md_theme_dark_tertiary,
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
                    Text(
                        text = transaction.date,
                        fontSize = 10.sp,
                        maxLines = 1,
                        fontFamily = Roboto,
                        fontWeight = FontWeight.Medium,
                        color = textColor,
                        modifier = Modifier.padding(end = 12.dp, bottom = 16.dp)
                    )

                }
            }
            if (showErrorMessage && transaction.transactionType is TransactionType.Failed) {
                Row {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Failure reason: ${transaction.failedTransactionMessage ?: ""}",
                        fontSize = 14.sp,
                        fontFamily = Roboto,
                        color = place_holder_text,
                        modifier = Modifier.padding(start = 60.dp, bottom = 16.dp)
                    )
                }
            }
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
