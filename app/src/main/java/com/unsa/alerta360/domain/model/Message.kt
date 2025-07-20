package com.unsa.alerta360.domain.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.unsa.alerta360.data.util.DateLongAdapter

data class Message(
    @SerializedName("_id")
    val id: String?,
    @SerializedName("chat_id")
    val chatId: String?,
    @SerializedName("sender_id")
    val senderId: String?,
    @SerializedName("sender_name")
    val senderName: String?,
    val text: String?,
    val messageType: String?,
    @SerializedName("createdAt")
    @JsonAdapter(DateLongAdapter::class)
    val timestamp: Long?
)
