package com.unsa.alerta360.data.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.unsa.alerta360.domain.repository.IncidentRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val syncManager: SyncManager,
    private val syncResultHandler: SyncResultHandler
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "sync_incidents"
        const val KEY_ITEMS_SYNCED = "items_synced"
        const val KEY_ERROR_MESSAGE = "error_message"
        private const val TAG = "SyncWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            android.util.Log.d(TAG, "Starting sync work")
            
            // Notificar que la sincronización está en progreso
            syncResultHandler.handleSyncResult(
                com.unsa.alerta360.data.sync.SyncResultHandler.SyncResult.InProgress
            )
            
            // Resetear incidentes que quedaron en estado "sincronizando"
            syncManager.resetSyncingIncidents()
            
            // Realizar la sincronización de incidentes pendientes
            val syncResult = syncManager.syncPendingIncidents()
            
            when (syncResult) {
                is com.unsa.alerta360.data.sync.SyncResult.Success -> {
                    android.util.Log.d(TAG, "Sync completed successfully: ${syncResult.syncedCount} items")
                    
                    // Manejar el resultado exitoso
                    syncResultHandler.handleSyncResult(
                        com.unsa.alerta360.data.sync.SyncResultHandler.SyncResult.Success(syncResult.syncedCount)
                    )
                    
                    // Cancelar notificación de progreso
                    syncResultHandler.cancelNotifications()
                    
                    return@withContext Result.success(
                        workDataOf(KEY_ITEMS_SYNCED to syncResult.syncedCount)
                    )
                }
                
                is com.unsa.alerta360.data.sync.SyncResult.Error -> {
                    android.util.Log.e(TAG, "Sync failed: ${syncResult.message}")
                    
                    // Manejar errores
                    syncResultHandler.handleSyncResult(
                        com.unsa.alerta360.data.sync.SyncResultHandler.SyncResult.Error(
                            message = syncResult.message
                        )
                    )
                    
                    return@withContext Result.failure(
                        workDataOf(KEY_ERROR_MESSAGE to syncResult.message)
                    )
                }
                
                is com.unsa.alerta360.data.sync.SyncResult.NoNetwork -> {
                    android.util.Log.w(TAG, "No network available for sync")
                    
                    // No es un error crítico, simplemente no hay red
                    syncResultHandler.cancelNotifications()
                    
                    return@withContext Result.success(
                        workDataOf(KEY_ITEMS_SYNCED to 0)
                    )
                }
            }
            
        } catch (exception: Exception) {
            android.util.Log.e(TAG, "Unexpected error during sync", exception)
            
            // Manejar errores inesperados
            syncResultHandler.handleSyncResult(
                com.unsa.alerta360.data.sync.SyncResultHandler.SyncResult.Error(
                    message = exception.message ?: "Error desconocido durante la sincronización"
                )
            )
            
            Result.failure(
                workDataOf(KEY_ERROR_MESSAGE to exception.message)
            )
        }
    }

}
