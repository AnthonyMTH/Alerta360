package com.unsa.alerta360.domain.repository

import com.unsa.alerta360.domain.model.Chat

interface ChatRepository {
    suspend fun getAllChats(): List<Chat>
}
