package chat.sphinx.common.viewmodel.chat


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import chat.sphinx.common.state.EditMessageState
import chat.sphinx.common.viewmodel.DashboardViewModel
import chat.sphinx.concepts.network.query.lightning.model.route.RouteSuccessProbabilityDto
import chat.sphinx.concepts.network.query.lightning.model.route.isRouteAvailable
import chat.sphinx.response.LoadResponse
import chat.sphinx.response.Response
import chat.sphinx.response.ResponseError
import chat.sphinx.wrapper.PhotoUrl
import chat.sphinx.wrapper.chat.Chat
import chat.sphinx.wrapper.chat.ChatName
import chat.sphinx.wrapper.contact.Contact
import chat.sphinx.wrapper.contact.ContactAlias
import chat.sphinx.wrapper.contact.getColorKey
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class ChatContactViewModel(
    chatId: ChatId?,
    val contactId: ContactId,
    dashboardViewModel: DashboardViewModel
): ChatViewModel(
    chatId,
    dashboardViewModel
) {
    override val chatSharedFlow: SharedFlow<Chat?> = flow {
        chatId?.let { chatId ->
            emitAll(chatRepository.getChatByIdFlow(chatId))
        } ?: repositoryDashboard.getConversationByContactIdFlow(contactId).collect { chat ->
            emit(chat)
        }
    }.distinctUntilChanged().shareIn(
        scope,
        SharingStarted.WhileSubscribed(2_000),
        replay = 1
    )

    private val contactSharedFlow: SharedFlow<Contact?> = flow {
        emitAll(contactRepository.getContactById(contactId))
    }.distinctUntilChanged().shareIn(
        scope,
        SharingStarted.WhileSubscribed(2_000),
        replay = 1,
    )

    override suspend fun getChatInfo(): Triple<ChatName?, PhotoUrl?, String>? {
        contactSharedFlow.replayCache.firstOrNull()?.let { contact ->
            return Triple(
                contact.alias?.value?.let { ChatName(it) },
                contact.photoUrl?.value?.let { PhotoUrl(it) },
                contact.getColorKey()
            )
        } ?: contactSharedFlow.firstOrNull()?.let { contact ->
            return Triple(
                contact.alias?.value?.let { ChatName(it) },
                contact.photoUrl?.value?.let { PhotoUrl(it) },
                contact.getColorKey()
            )
        } ?: let {
            var alias: ContactAlias? = null
            var photoUrl: PhotoUrl? = null
            var colorKey: String = getRandomHexCode()

            try {
                contactSharedFlow.collect { contact ->
                    if (contact != null) {
                        alias = contact.alias
                        photoUrl = contact.photoUrl
                        colorKey = contact.getColorKey()
                        throw Exception()
                    }
                }
            } catch (e: Exception) {}
            delay(25L)

            return Triple(
                alias?.value?.let { ChatName(it) },
                photoUrl?.value?.let { PhotoUrl(it) },
                colorKey
            )
        }
    }

    override suspend fun getContact(): Contact? {
        return contactId?.let { contactRepository.getContactById(it).firstOrNull() }
    }

    override val checkRoute: Flow<LoadResponse<Boolean, ResponseError>> = flow {
        emit(LoadResponse.Loading)

        val networkFlow: Flow<LoadResponse<RouteSuccessProbabilityDto, ResponseError>>? = let {
            emit(LoadResponse.Loading)

            var contact: Contact? = contactSharedFlow.replayCache.firstOrNull()
                ?: contactSharedFlow.firstOrNull()

            if (contact == null) {
                try {
                    contactSharedFlow.collect {
                        if (contact != null) {
                            contact = it
                            throw Exception()
                        }
                    }
                } catch (e: Exception) {}
                delay(25L)
            }

            contact?.let { nnContact ->
                nnContact.nodePubKey?.let { pubKey ->

                    nnContact.routeHint?.let { hint ->

                        networkQueryLightning.checkRoute(pubKey, hint)

                    } ?: networkQueryLightning.checkRoute(pubKey)

                }
            }
        }

        networkFlow?.let { flow ->
            flow.collect { response ->
                when (response) {
                    LoadResponse.Loading -> {}
                    is Response.Error -> {
                        emit(response)
                    }
                    is Response.Success -> {
                        emit(
                            Response.Success(response.value.isRouteAvailable)
                        )
                    }
                }
            }
        } ?: emit(Response.Error(
            ResponseError("Contact and chatId were null, unable to check route")
        ))
    }

    override var editMessageState: EditMessageState by mutableStateOf(initialState())

    override fun initialState(): EditMessageState = EditMessageState(
        chatId = chatId,
        contactId = contactId,
    )

}