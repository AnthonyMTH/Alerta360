package com.unsa.alerta360.domain.usecase.chat

import com.unsa.alerta360.domain.model.Chat
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.repository.ChatRepository
import javax.inject.Inject

class GetAllChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(): Result<List<Chat>> {
        return try {
            val chats = chatRepository.getAllChats()
            Result.Success(chats)
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Error al obtener los chats: ${e.message}"
            )
        }
    }
}
