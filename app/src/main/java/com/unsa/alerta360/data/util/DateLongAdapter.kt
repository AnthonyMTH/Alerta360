package com.unsa.alerta360.data.util

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import java.time.Instant
import java.time.format.DateTimeParseException

class DateLongAdapter : JsonSerializer<Long?>, JsonDeserializer<Long?> {

    override fun serialize(src: Long?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return if (src == null) {
            JsonPrimitive("") // Or JsonNull.INSTANCE if you prefer null in JSON
        } else {
            JsonPrimitive(Instant.ofEpochMilli(src).toString())
        }
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Long? {
        if (json == null || json.isJsonNull || json.asString.isNullOrEmpty()) {
            return null
        }
        return try {
            Instant.parse(json.asString).toEpochMilli()
        } catch (e: DateTimeParseException) {
            // Log the error or handle it as appropriate
            null
        } catch (e: NumberFormatException) {
            // Handle cases where it might be a number string but not a valid date
            null
        }
    }
}
