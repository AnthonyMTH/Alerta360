package com.unsa.alerta360.data.local.entity
import androidx.room.*
import com.unsa.alerta360.data.local.room.Converters
import java.util.UUID


@Entity(tableName = "incident")
@TypeConverters(Converters::class)
data class IncidentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
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
    @ColumnInfo(name = "__v") val version: Int?,
    val synced: Boolean = false
)