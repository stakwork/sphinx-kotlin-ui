package chat.sphinx.common.state

sealed class ContactScreenState {
    object Choose : ContactScreenState()
    object NewToSphinx : ContactScreenState()
    object AlreadyOnSphinx : ContactScreenState()
    object EditContact : ContactScreenState()


}