package com.unsa.alerta360.data.local.entity
import androidx.room.*
import kotlinx.serialization.encodeToString
import kotlinx.datetime.Instant
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Entity(tableName = "incidents")
@TypeConverters(IncidentConverters::class)
data class Incident(
    @PrimaryKey  @ColumnInfo(name = "id")          val id: String,
    @ColumnInfo(name = "title")                    val title: String,
    @ColumnInfo(name = "description")              val description: String,
    @ColumnInfo(name = "incident_type")            val incidentType: String,
    @ColumnInfo(name = "ubication")                val ubication: String,
    /** lat,lng como texto; si prefieres dos columnas usa dos Doubles  */
    @ColumnInfo(name = "geolocation")              val geolocation: String,
    /** se guarda como JSON para que no duplique filas al re-insertar (idempotencia) */
    @ColumnInfo(name = "evidence")                 val evidence: List<String>,
    @ColumnInfo(name = "district")                 val district: String,
    @ColumnInfo(name = "user_id")                  val userId: String,
    @ColumnInfo(name = "created_at")               val createdAt: Instant? = null,
    @ColumnInfo(name = "updated_at")               val updatedAt: Instant? = null,
    @ColumnInfo(name = "version")                  val version: Int? = null
)

class IncidentConverters {
    private val json = Json                         // kotlinx-serialization
    @TypeConverter fun listToString(l: List<String>?): String =
        json.encodeToString(l ?: emptyList())
    @TypeConverter fun stringToList(s: String): List<String> =
        json.decodeFromString(s)

    @TypeConverter fun instantToLong(i: Instant?): Long? = i?.toEpochMilliseconds()
    @TypeConverter fun longToInstant(l: Long?): Instant? =
        l?.let { Instant.fromEpochMilliseconds(it) }
}