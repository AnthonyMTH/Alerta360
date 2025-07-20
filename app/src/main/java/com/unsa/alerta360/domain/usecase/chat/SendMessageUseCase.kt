package com.unsa.alerta360.domain.usecase.chat

import com.unsa.alerta360.domain.model.Message
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.repository.MessageRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val messageRepository: MessageRepository
) {
    suspend operator fun invoke(message: Message): Result<Message> {
        return try {
            val sentMessage = messageRepository.sendMessage(message)
            Result.Success(sentMessage)
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Error al enviar el mensaje: ${e.message}"
            )
        }
    }
}
