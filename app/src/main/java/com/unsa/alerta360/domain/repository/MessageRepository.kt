package com.unsa.alerta360.domain.repository

import com.unsa.alerta360.domain.model.Message

interface MessageRepository {
    suspend fun getMessagesForChat(chatId: String): List<Message>
    suspend fun sendMessage(message: Message): Message
}
