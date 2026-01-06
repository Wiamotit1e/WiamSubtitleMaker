package org.wiamotit1e.user_interaction

class DoForMessage(
    private val onSuccess: (Message.Success) -> Unit = {},
    private val onFailure: (Message.Failure) -> Unit = {},
    private val onWarning: (Message.Warning) -> Unit = {},
    private val onInfo: (Message.Info) -> Unit = {}
) {
    
    fun doForMessage(message: Message) {
        when (message) {
            is Message.Success -> onSuccess(message)
            is Message.Failure -> onFailure(message)
            is Message.Warning -> onWarning(message)
            is Message.Info -> onInfo(message)
        }
    }
}