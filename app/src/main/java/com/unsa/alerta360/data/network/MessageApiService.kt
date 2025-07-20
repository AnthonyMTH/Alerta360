package com.unsa.alerta360.data.network

import com.unsa.alerta360.data.model.MessageDto
import com.unsa.alerta360.data.model.MessageListResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageApiService {
    @GET("message/{chatId}")
    suspend fun getMessagesForChat(@Path("chatId") chatId: String): MessageListResponse

    @POST("message/create")
    suspend fun sendMessage(
        @Body message: MessageDto
    ): MessageDto
}
