package org.wiamotit1e.user_interaction

sealed class Message {
    data class Success(val data: Any): Message()
    data class Failure(val error: String) : Message()
    data class Warning(val content: String): Message()
    data class Info(val content: String): Message()
}