package com.unsa.alerta360.data.repository

import android.util.Log
import com.unsa.alerta360.data.mapper.toDomain
import com.unsa.alerta360.data.mapper.toDto
import com.unsa.alerta360.data.network.MessageApiService
import com.unsa.alerta360.domain.model.Message
import com.unsa.alerta360.domain.repository.MessageRepository
import javax.inject.Inject

class MessageRepositoryImpl @Inject constructor(
    private val messageApiService: MessageApiService
) : MessageRepository {

    override suspend fun getMessagesForChat(chatId: String): List<Message> {
        val messageDtos = messageApiService.getMessagesForChat(chatId).messages
        Log.d("MessageRepo", "Received Message DTOs: $messageDtos")
        return messageDtos.map { it.toDomain() }
    }

    override suspend fun sendMessage(message: Message): Message {
        return messageApiService.sendMessage(message.toDto()).toDomain()
    }
}
