package com.unsa.alerta360.data.network

import com.unsa.alerta360.data.model.ChatDto
import retrofit2.http.GET

interface ChatApiService {
    @GET("chat/district/all")
    suspend fun getAllChats(): List<ChatDto>
}
