package com.unsa.alerta360.presentation.chat

import androidx.lifecycle.ViewModel
import com.unsa.alerta360.domain.model.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatViewModel : ViewModel() {

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun loadMessages(chatId: String) {
        // TODO: Implement message loading from a repository
        _messages.value = listOf(
            Message("1", "Hola a todos!", "Juan Perez", System.currentTimeMillis()),
            Message("2", "Bienvenidos al chat de Arequipa!", "Admin", System.currentTimeMillis() + 1000),
        )
    }

    fun sendMessage(text: String) {
        // TODO: Implement message sending
        val newMessage = Message(
            id = (_messages.value.size + 1).toString(),
            text = text,
            senderName = "Me", // Replace with actual user name
            timestamp = System.currentTimeMillis()
        )
        _messages.value = _messages.value + newMessage
    }
}
