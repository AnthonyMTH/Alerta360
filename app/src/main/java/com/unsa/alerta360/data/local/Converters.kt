package com.unsa.alerta360.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromEvidenceList(value: List<String>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toEvidenceList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType)
    }
}