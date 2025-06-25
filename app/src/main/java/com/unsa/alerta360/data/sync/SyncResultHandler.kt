package com.unsa.alerta360.data.sync

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.unsa.alerta360.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncResultHandler @Inject constructor(
    private val context: Context
) {
    
    companion object {
        private const val SYNC_NOTIFICATION_CHANNEL_ID = "sync_channel"
        private const val SYNC_NOTIFICATION_ID = 1001
    }
    
    init {
        createNotificationChannel()
    }
    
    fun handleSyncResult(result: SyncResult) {
        when (result) {
            is SyncResult.Success -> handleSyncSuccess(result)
            is SyncResult.Error -> handleSyncError(result)
            is SyncResult.InProgress -> handleSyncInProgress()
        }
    }
    
    private fun handleSyncSuccess(result: SyncResult.Success) {
        if (result.itemsSynced > 0) {
            showNotification(
                title = "Sincronización completada",
                message = "${result.itemsSynced} incidente(s) sincronizado(s)",
                isError = false
            )
        }
    }
    
    private fun handleSyncError(result: SyncResult.Error) {
        showNotification(
            title = "Error de sincronización",
            message = result.message,
            isError = true
        )
    }
    
    private fun handleSyncInProgress() {
        // Opcionalmente mostrar una notificación de progreso
        showNotification(
            title = "Sincronizando...",
            message = "Sincronizando datos con el servidor",
            isError = false,
            ongoing = true
        )
    }
    
    private fun showNotification(
        title: String,
        message: String,
        isError: Boolean,
        ongoing: Boolean = false
    ) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        
        val notification = NotificationCompat.Builder(context, SYNC_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(if (isError) android.R.drawable.ic_dialog_alert else android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(!ongoing)
            .setOngoing(ongoing)
            .build()
        
        notificationManager.notify(SYNC_NOTIFICATION_ID, notification)
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SYNC_NOTIFICATION_CHANNEL_ID,
                "Sincronización",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones de sincronización de datos"
            }
            
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    fun cancelNotifications() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(SYNC_NOTIFICATION_ID)
    }
}

sealed class SyncResult {
    data class Success(val itemsSynced: Int) : SyncResult()
    data class Error(val message: String, val throwable: Throwable? = null) : SyncResult()
    object InProgress : SyncResult()
}
