package com.unsa.alerta360.data.mapper

import java.time.Instant
import java.time.format.DateTimeParseException

import com.unsa.alerta360.data.model.MessageDto
import com.unsa.alerta360.domain.model.Message

fun MessageDto.toDomain(): Message {
    val parsedTimestamp = if (timestamp != null) {
        try {
            Instant.parse(timestamp).toEpochMilli()
        } catch (e: DateTimeParseException) {
            0L // Default value if parsing fails
        }
    } else {
        0L // Default value if timestamp from DTO is null
    }

    return Message(
        id = _id,
        chatId = chat_id,
        senderId = sender_id,
        senderName = sender_name,
        text = text,
        messageType = messageType,
        timestamp = parsedTimestamp
    )
}

fun Message.toDto(): MessageDto {
    val formattedTimestamp =
        timestamp?.let { Instant.ofEpochMilli(it).atOffset(java.time.ZoneOffset.UTC).format(java.time.format.DateTimeFormatter.ISO_INSTANT) }
    return MessageDto(
        _id = id,
        chat_id = chatId,
        sender_id = senderId,
        sender_name = senderName,
        text = text,
        messageType = messageType,
        timestamp = formattedTimestamp
    )
}
