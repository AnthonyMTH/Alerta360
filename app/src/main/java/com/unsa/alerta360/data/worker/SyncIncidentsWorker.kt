package com.unsa.alerta360.data.worker

import android.content.Context
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
        pendings.forEach { entity ->
            try {
                val resp = api.createIncident(entity.toDto())
                if (resp.isSuccessful) {
                    val remote = resp.body()!!
                    dao.insert(remote.toEntity().copy(synced = true))
                }
            } catch (_: Exception) { /* retry later */ }
        }
        return Result.success()
    }
}
