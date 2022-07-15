package chat.sphinx.common.components


import Roboto
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.landing.AddContactWindow
import chat.sphinx.common.state.ContactScreenState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import com.example.compose.place_holder_text
import com.example.compose.primary_green
import com.example.compose.primary_red

@Composable
fun DashboardSidebarUI(dashboardViewModel: DashboardViewModel) {
    Box(
        Modifier
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        Column {
            TopAppBar(
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                elevation = 8.dp
            ) {
                Row(modifier = Modifier.fillMaxHeight().width(32.dp), verticalAlignment = Alignment.CenterVertically) {
                    CompositionLocalProvider(
                        LocalContentAlpha provides ContentAlpha.high,
                        content = {
                            IconButton(onClick = dashboardViewModel::networkRefresh) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Refresh Connection",
                                    tint = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    )
                }

                Row(
                    Modifier.fillMaxHeight().weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ProvideTextStyle(value = MaterialTheme.typography.h6) {
                        CompositionLocalProvider(
                            LocalContentAlpha provides ContentAlpha.high,
                            content = {
                                val balance by dashboardViewModel.balanceStateFlow.collectAsState()
                                Text(
                                    text = "${balance?.balance?.value ?: 0} sats",
                                    fontSize = 14.sp,
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                                    textAlign = TextAlign.Center,
                                )
                            }
                        )
                    }
                }

                CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                    Row(
                        Modifier.fillMaxHeight().width(32.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            IconButton(
                                onClick = {}
                            ) {
                                val networkState by dashboardViewModel.networkStateFlow.collectAsState()
                                if (networkState is LoadResponse.Loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.FlashOn,
                                        contentDescription = "Connection Status",
                                        tint = if (networkState is Response.Success) {
                                            primary_green
                                        } else {
                                            primary_red
                                        },
                                        modifier = Modifier.size(16.dp)
                                    )

                                }
                            }
                        }
                    )
                }
            }
            var searchText by rememberSaveable { mutableStateOf("") }
            TopAppBar(
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                title = {
                    CustomTextField(
                        leadingIcon = {
                            Icon(
                                Icons.Filled.Search,
                                null,
                                modifier = Modifier.width(30.dp),
                                tint = place_holder_text
                            )
                        },
                        trailingIcon = null,
                        modifier = Modifier
                            .background(
                                Color(0xFf151e27),
                                RoundedCornerShape(percent = 50)
                            )
                            .padding(4.dp)
                            .height(30.dp),
                        fontSize = 14.sp,
                        placeholderText = "Search",
                        onValueChange = { input ->
                            searchText = input
                        },
                        value = searchText
                    )
                },
                elevation = 8.dp,
                actions = {
                    IconButton(onClick = {
                        dashboardViewModel.toggleContactWindow(true, ContactScreenState.Choose)
                    }) {
                        Icon(
                            Icons.Default.PersonAddAlt,
                            contentDescription = "Add a person",
                            tint = place_holder_text
                        )
                    }
                }
            )
            val addContactWindowState by dashboardViewModel.contactWindowStateFlow.collectAsState()
            if (addContactWindowState.first){
                AddContactWindow(dashboardViewModel)
            }
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
    color: Color? = null,
    onValueChange: (String) -> Unit,
    fontSize: TextUnit = MaterialTheme.typography.body2.fontSize
) {
    BasicTextField(
        modifier = modifier.fillMaxWidth(),
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        cursorBrush = SolidColor(MaterialTheme.colors.primary),
        textStyle = LocalTextStyle.current.copy(
            fontFamily = Roboto,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            color = color ?: place_holder_text
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
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground.copy(alpha = .7f),
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