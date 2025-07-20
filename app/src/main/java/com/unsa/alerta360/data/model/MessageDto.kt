package com.unsa.alerta360.data.model

data class MessageDto(
    val _id: String?,
    val chat_id: String?,
    val sender_id: String?,
    val sender_name: String?,
    val text: String?,
    val messageType: String?,
    val timestamp: String?
)
