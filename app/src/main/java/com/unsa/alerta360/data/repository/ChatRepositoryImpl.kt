package com.unsa.alerta360.data.repository

import com.unsa.alerta360.data.mapper.toDomain
import com.unsa.alerta360.data.network.ChatApiService
import com.unsa.alerta360.domain.model.Chat
import com.unsa.alerta360.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatApiService: ChatApiService
) : ChatRepository {

    override suspend fun getAllChats(): List<Chat> {
        return chatApiService.getAllChats().map { it.toDomain() }
    }
}
