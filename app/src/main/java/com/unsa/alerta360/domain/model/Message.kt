package com.unsa.alerta360.domain.model

data class Message(
    val id: String,
    val text: String,
    val senderName: String,
    val timestamp: Long
)
