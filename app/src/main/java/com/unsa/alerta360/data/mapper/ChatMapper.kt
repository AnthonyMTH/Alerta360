package com.unsa.alerta360.data.mapper

import java.time.Instant
import java.time.format.DateTimeParseException

import com.unsa.alerta360.data.model.ChatDto
import com.unsa.alerta360.data.model.LastMessageDto
import com.unsa.alerta360.domain.model.Chat
import com.unsa.alerta360.domain.model.LastMessage

fun ChatDto.toDomain(): Chat {
    return Chat(
        id = _id,
        chatType = chatType,
        districtName = districtName,
        chatName = chatName,
        description = description,
        isActive = isActive,
        lastMessage = lastMessage?.toDomain(),
        messageCount = messageCount
    )
}

fun LastMessageDto.toDomain(): LastMessage {
    val parsedTimestamp = try {
        timestamp?.let { Instant.parse(it).toEpochMilli() }
    } catch (e: DateTimeParseException) {
        // Log the error or handle it as appropriate for your application
        // For now, returning null or a default value
        null
    }
    return LastMessage(
        text = text,
        senderId = sender_id,
        senderName = sender_name,
        timestamp = parsedTimestamp
    )
}

fun Chat.toDto(): ChatDto {
    return ChatDto(
        _id = id,
        chatType = chatType,
        districtName = districtName,
        chatName = chatName,
        description = description,
        isActive = isActive,
        lastMessage = lastMessage?.toDto(),
        messageCount = messageCount
    )
}

fun LastMessage.toDto(): LastMessageDto {
    val formattedTimestamp = timestamp?.let {
        Instant.ofEpochMilli(it).atOffset(java.time.ZoneOffset.UTC).format(java.time.format.DateTimeFormatter.ISO_INSTANT)
    }
    return LastMessageDto(
        text = text,
        sender_id = senderId,
        sender_name = senderName,
        timestamp = formattedTimestamp
    )
}
