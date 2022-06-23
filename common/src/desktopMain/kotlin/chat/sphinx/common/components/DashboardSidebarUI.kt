package chat.sphinx.common.components


import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.di.container.SphinxContainer
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import kotlinx.coroutines.launch

@Composable
fun DashboardSidebarUI(dashboardViewModel: DashboardViewModel) {
    val scope = SphinxContainer.appModule.applicationScope
    val dispatchers = SphinxContainer.appModule.dispatchers
    var text by remember { mutableStateOf(TextFieldValue("")) }
    val balance = mutableStateOf(0L)
    // TODO Fix compilation issue
//    LaunchedEffect(key1 = ""){
//        scope.launch(dispatchers.main) {
//            dashboardViewModel.getAccountBalance().collect {
//                it?.let { nodeBalance ->
//                    balance.value = nodeBalance.balance.value
//                }
//            }
//        }
//    }
    Box(
        Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Column {
            TopAppBar(
                title = {
                    Text(
                        text = "${balance.value} sats",
                        fontSize = 14.sp,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary
                    )
                },
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                elevation = 8.dp,
                navigationIcon = {
                    IconButton(onClick = dashboardViewModel::networkRefresh) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh Connection",
                            tint = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {}
                    ) {
                        val networkState by dashboardViewModel.networkStateFlow.collectAsState()
                        if (networkState is LoadResponse.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(14.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.FlashOn,
                                contentDescription = "Connection Status",
                                tint = if (networkState is Response.Success) {
                                    Color.Green
                                } else {
                                    androidx.compose.material3.MaterialTheme.colorScheme.error
                                },
                                modifier = Modifier.size(14.dp)
                            )

                        }
                    }
                }
            )

            var searchText by rememberSaveable { mutableStateOf("") }
            TopAppBar(
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                title = {

                    CustomTextField(
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search,
                                null,
                                tint = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                            )
                        },
                        trailingIcon = null,
                        modifier = Modifier
                            .background(
                                Color(0xFf151e27),
                                RoundedCornerShape(percent = 50)
                            )
                            .padding(4.dp)
                            .height(20.dp),
                        fontSize = 10.sp,
                        placeholderText = "Search", onValueChange = {}, value = ""
                    )
                },
                elevation = 8.dp,
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Default.PersonAddAlt,
                            contentDescription = "Add a person",
                            tint = Color.Gray
                        )
                    }
                }
            )

            ChatListUI()
        }
    }
}

@Preview
@Composable
fun DashboardSidebarPreview() {
    MaterialTheme {
        DashboardSidebarUI(DashboardViewModel())
    }
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    placeholderText: String = "Placeholder",
    value: String,
    onValueChange: (String) -> Unit,
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize
) {
    BasicTextField(modifier = modifier
//        .background(
//            Color(0xFF151e27),
////            RoundedCornerShape(percent = 50)
//        )
        .fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colors.secondary,
            fontSize = fontSize
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) leadingIcon()
                Box(Modifier.weight(1f)) {
                    if (value.isEmpty())
                        Text(
                            placeholderText,
                            style = LocalTextStyle.current.copy(
                                color =       androidx.compose.material3.MaterialTheme.colorScheme.onBackground.copy(alpha = .7f),
                                fontSize = fontSize
                            )
                        )
                    innerTextField()
                }
                if (trailingIcon != null) trailingIcon()
            }
        }
    )
}