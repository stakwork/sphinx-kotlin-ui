package chat.sphinx.common.components


import Roboto
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import chat.sphinx.common.components.profile.Profile
import chat.sphinx.common.components.tribe.CreateTribeView
import chat.sphinx.common.components.tribe.JoinTribeView
import chat.sphinx.common.components.tribe.TribeDetailView
import chat.sphinx.common.state.AuthorizeViewState
import chat.sphinx.common.state.ContactScreenState
import chat.sphinx.common.state.ContentState
import chat.sphinx.common.state.ContentState.scope
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.common.viewmodel.WebAppViewModel
import chat.sphinx.common.viewmodel.contact.QRCodeViewModel
import chat.sphinx.common.viewmodel.dashboard.ChatListViewModel
import chat.sphinx.concepts.repository.connect_manager.model.NetworkStatus
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.utils.SphinxFonts
import chat.sphinx.wrapper.lightning.asFormattedString
import chat.sphinx.wrapper.message.media.isImage
import kotlinx.coroutines.launch
import theme.place_holder_text
import theme.primary_green
import theme.primary_red
import theme.sphinx_orange
import utils.deduceMediaType

@Composable
fun DashboardSidebarUI(
    dashboardViewModel: DashboardViewModel,
    webAppViewModel: WebAppViewModel,
) {
    val chatListViewModel = remember { ChatListViewModel() }
    val uriHandler = LocalUriHandler.current

    Box(
        Modifier
            .background(androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
            .fillMaxSize()
    ) {
        Column(modifier = Modifier.background(androidx.compose.material3.MaterialTheme.colorScheme.background)) {
            TopAppBar(
                backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                contentColor = androidx.compose.material3.MaterialTheme.colorScheme.background,
                elevation = 8.dp,
                modifier = Modifier.padding(8.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    PhotoUrlImage(
                        photoUrl = null,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(verticalArrangement = Arrangement.Center) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            androidx.compose.material3.Text(
                                text = "Alejandro",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontWeight = FontWeight.W600,
                                fontFamily = SphinxFonts.montserratFamily,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))

                            val networkState by dashboardViewModel.networkStatusStateFlow.collectAsState()
                            if (networkState is NetworkStatus.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = androidx.compose.material3.MaterialTheme.colorScheme.tertiary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    Icons.Default.FlashOn,
                                    contentDescription = "Connection Status",
                                    tint = if (networkState is NetworkStatus.Connected) {
                                        primary_green
                                    } else {
                                        primary_red
                                    },
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            val balance by dashboardViewModel.balanceStateFlow.collectAsState()
                            androidx.compose.material3.Text(
                                text = balance?.balance?.asFormattedString(' ') ?: "0",
                                color = MaterialTheme.colorScheme.tertiary,
                                fontFamily = SphinxFonts.montserratFamily,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            androidx.compose.material3.Text(
                                "sat",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontFamily = SphinxFonts.montserratFamily,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
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
                        trailingIcon = {
                            if (chatListViewModel.searchText.value?.text?.isNotEmpty() == true) {
                                Icon(
                                    Icons.Filled.Cancel,
                                    null,
                                    tint = place_holder_text,
                                    modifier = Modifier.width(16.dp).clickable {
                                        chatListViewModel.filterChats(TextFieldValue(""))
                                    },
                                )
                            }
                        },
                        modifier = Modifier
                            .background(
                                androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                RoundedCornerShape(percent = 50)
                            )
                            .padding(4.dp)
                            .height(30.dp),
                        fontSize = 14.sp,
                        placeholderText = "Search",
                        onValueChange = { input ->
                            chatListViewModel.filterChats(input)
                        },
                        value = chatListViewModel.searchText.value ?: TextFieldValue("")
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

            ChatListUI(
                chatListViewModel,
                dashboardViewModel
            )

            AboutSphinxWindow(dashboardViewModel)
            AddContactWindow(dashboardViewModel)
            ProfileWindow(dashboardViewModel)
            TransactionsWindow(dashboardViewModel)
            TribeDetailWindow(dashboardViewModel)
            CreateTribeWindow(dashboardViewModel)
            QRWindow(dashboardViewModel)
            JoinTribeWindow(dashboardViewModel)
            WebAppWindow(dashboardViewModel, webAppViewModel)
            AuthorizeWindow(webAppViewModel)
        }
    }
}

@Composable
fun AboutSphinxWindow(
    dashboardViewModel: DashboardViewModel
) {
    val aboutSphinxWindowState by dashboardViewModel.aboutSphinxStateFlow.collectAsState()
    if (aboutSphinxWindowState) {
        AboutSphinx(dashboardViewModel)
    }
}

@Composable
fun AddContactWindow(
    dashboardViewModel: DashboardViewModel
) {
    val addContactWindowState by dashboardViewModel.contactWindowStateFlow.collectAsState()
    if (addContactWindowState.first) {
        AddContactWindowUI(dashboardViewModel)
    }
}

@Composable
fun ProfileWindow(
    dashboardViewModel: DashboardViewModel
) {
    val profileWindowState by dashboardViewModel.profileStateFlow.collectAsState()
    if (profileWindowState) {
        Profile(dashboardViewModel)
    }
}

@Composable
fun TransactionsWindow(
    dashboardViewModel: DashboardViewModel
) {
    val transactionsWindowState by dashboardViewModel.transactionsStateFlow.collectAsState()
    if (transactionsWindowState) {
        TransactionsUI(dashboardViewModel)
    }
}

@Composable
fun TribeDetailWindow(
    dashboardViewModel: DashboardViewModel
) {
    val tribeWindowState by dashboardViewModel.tribeDetailStateFlow.collectAsState()
    if (tribeWindowState.first) {
        tribeWindowState.second?.let { chatId ->
            TribeDetailView(dashboardViewModel, chatId)
        }
    }
}

@Composable
fun CreateTribeWindow(
    dashboardViewModel: DashboardViewModel
) {
    val createTribeWindowState by dashboardViewModel.createTribeStateFlow.collectAsState()
    if (createTribeWindowState.first) {
        if (createTribeWindowState.second != null) {
            CreateTribeView(dashboardViewModel, createTribeWindowState.second)
        } else {
            CreateTribeView(dashboardViewModel, null)
        }
    }
}

@Composable
fun QRWindow(
    dashboardViewModel: DashboardViewModel
) {
    val qrWindowState by dashboardViewModel.qrWindowStateFlow.collectAsState()
    if (qrWindowState.first) {
        qrWindowState.second?.let { titleAndValue ->
            QRDetail(dashboardViewModel, QRCodeViewModel(titleAndValue.first, titleAndValue.second))
        }
    }
}

@Composable
fun JoinTribeWindow(
    dashboardViewModel: DashboardViewModel
) {
    val joinTribeWindowState by dashboardViewModel.joinTribeStateFlow.collectAsState()
    if (joinTribeWindowState.first) {
        joinTribeWindowState.second?.let { joinTribeLink ->
            JoinTribeView(dashboardViewModel, joinTribeLink)
        }
    }
}

@Composable
fun WebAppWindow(
    dashboardViewModel: DashboardViewModel,
    webAppViewModel: WebAppViewModel
) {
    val webAppWindowState by webAppViewModel.webAppWindowStateFlow.collectAsState()
    if (webAppWindowState) {
        WebAppUI(dashboardViewModel, webAppViewModel)
    }
}

@Composable
fun AuthorizeWindow(
    webAppViewModel: WebAppViewModel
) {
    val authorizeView by webAppViewModel.authorizeViewStateFlow.collectAsState()
    (authorizeView as? AuthorizeViewState.Opened)?.let {
        AuthorizeViewUI(webAppViewModel, it.budgetField)
    }
}