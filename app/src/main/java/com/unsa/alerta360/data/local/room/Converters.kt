package com.unsa.alerta360.data.local.room


import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {
    private val gson = Gson()

    @TypeConverter
    @JvmStatic
    fun fromEvidenceList(evidence: List<String>?): String =
        gson.toJson(evidence)

    @TypeConverter
    @JvmStatic
    fun toEvidenceList(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }
}