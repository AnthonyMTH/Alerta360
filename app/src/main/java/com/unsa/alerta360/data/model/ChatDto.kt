package com.unsa.alerta360.data.model

data class ChatDto(
    val _id: String,
    val chatType: String,
    val districtName: String?,
    val chatName: String,
    val description: String?,
    val isActive: Boolean,
    val lastMessage: LastMessageDto?,
    val messageCount: Int
)
