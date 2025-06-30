package com.unsa.alerta360.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.unsa.alerta360.data.local.dao.IncidentDao
import com.unsa.alerta360.data.mapper.toDto
import com.unsa.alerta360.data.mapper.toEntity
import com.unsa.alerta360.data.network.IncidentApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncIncidentsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val dao: IncidentDao,
    private val api: IncidentApi
): CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val pendings = dao.getPending()
        var successCount = 0

        pendings.forEach { entity ->
            try {
                val resp = api.createIncident(entity.toDto())
                if (resp.isSuccessful) {
                    val remote = resp.body()!!
                    // Eliminar el registro local pendiente
                    dao.deleteById(entity.id)
                    // Insertar el registro sincronizado con ID del servidor
                    dao.insert(remote.toEntity().copy(synced = true))
                    successCount++
                }
            } catch (e: Exception) {
                Log.e("SyncWorker", "Error syncing incident ${entity.id}: ${e.message}")
            }
        }
        return if (successCount > 0) Result.success() else Result.retry()
    }
}
