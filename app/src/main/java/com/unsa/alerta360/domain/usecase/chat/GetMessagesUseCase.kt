package com.unsa.alerta360.domain.usecase.chat

import com.unsa.alerta360.domain.model.Message
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.repository.MessageRepository
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(chatId: String): Result<List<Message>> {
        return try {
            val messages = messageRepository.getMessagesForChat(chatId)
            Result.Success(messages)
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Error al obtener los mensajes: ${e::class.simpleName} - ${e.message}"
            )
        }
    }
}
