package com.unsa.alerta360.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.unsa.alerta360.domain.repository.FcmRepository
import com.unsa.alerta360.domain.repository.IncidentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncIncidentsWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val incidentRepository: IncidentRepository,
    private val fcmRepository: FcmRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val TAG = "SyncIncidentsWorker"
    }

    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting incident synchronization")

            // Sincronizar incidentes
            incidentRepository.getAllIncidents()

            // Verificar y actualizar token FCM si es necesario
            val forceSync = inputData.getBoolean("force_sync", false)
            val userId = inputData.getString("user_id")

            if (forceSync && userId != null) {
                fcmRepository.getCurrentToken()?.let { token ->
                    fcmRepository.sendTokenToServer(userId, token)
                }
            }

            Log.d(TAG, "Incident synchronization completed successfully")
            Result.success()
        } catch (exception: Exception) {
            Log.e(TAG, "Error during sync", exception)
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }
}
