package com.unsa.alerta360.domain.model

data class Chat(
    val id: String,
    val chatType: String,
    val districtName: String?,
    val chatName: String,
    val description: String?,
    val isActive: Boolean,
    val lastMessage: LastMessage?,
    val messageCount: Int
)

