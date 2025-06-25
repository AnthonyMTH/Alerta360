package com.unsa.alerta360.data.sync

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncScheduler @Inject constructor(
    private val context: Context
) {
    
    private val workManager = WorkManager.getInstance(context)
    
    companion object {
        private const val SYNC_WORK_TAG = "sync_work"
        private const val PERIODIC_SYNC_WORK_NAME = "periodic_sync"
        private const val IMMEDIATE_SYNC_WORK_NAME = "immediate_sync"
    }
    
    /**
     * Inicia la sincronización periódica automática
     */
    fun startPeriodicSync(intervalHours: Long = 6) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val periodicSyncRequest = PeriodicWorkRequestBuilder<SyncWorker>(
            intervalHours, TimeUnit.HOURS
        )
            .setConstraints(constraints)
            .addTag(SYNC_WORK_TAG)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicSyncRequest
        )
    }
    
    /**
     * Ejecuta una sincronización inmediata
     */
    fun syncNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val immediateSyncRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .addTag(SYNC_WORK_TAG)
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        
        workManager.enqueueUniqueWork(
            IMMEDIATE_SYNC_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            immediateSyncRequest
        )
    }
    
    /**
     * Cancela todas las sincronizaciones programadas
     */
    fun cancelAllSync() {
        workManager.cancelAllWorkByTag(SYNC_WORK_TAG)
    }
    
    /**
     * Obtiene el estado de la sincronización periódica
     */
    fun getPeriodicSyncStatus() = workManager.getWorkInfosForUniqueWorkLiveData(PERIODIC_SYNC_WORK_NAME)
    
    /**
     * Obtiene el estado de la sincronización inmediata
     */
    fun getImmediateSyncStatus() = workManager.getWorkInfosForUniqueWorkLiveData(IMMEDIATE_SYNC_WORK_NAME)
}
