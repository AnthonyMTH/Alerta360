package com.unsa.alerta360.data.local.entity
import androidx.room.*
import com.unsa.alerta360.data.local.room.Converters
import kotlinx.serialization.encodeToString
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


@Entity(tableName = "incident")
@TypeConverters(Converters::class)
data class IncidentEntity(
    @PrimaryKey @ColumnInfo(name = "_id") val id: String,
    val description: String,
    val incidentType: String,
    val ubication: String,
    val geolocation: String,
    val district: String,
    val title: String,
    @ColumnInfo(name = "user_id") val userId: String,
    val evidence: List<String>,
    val createdAt: String?,
    val updatedAt: String?,
    @ColumnInfo(name = "__v") val version: Int
)