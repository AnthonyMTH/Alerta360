package com.unsa.alerta360.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.unsa.alerta360.MainActivity

class Alerta360FirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "ALERTA360_INCIDENTS"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "🚀 FCM Service CREATED!")
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "🔔 FCM Message received!")
        Log.d(TAG, "📨 From: ${remoteMessage.from}")
        Log.d(TAG, "📋 Message data: ${remoteMessage.data}")

        // Log de la notificación si existe
        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "🔔 Notification title: ${notification.title}")
            Log.d(TAG, "📝 Notification body: ${notification.body}")
        }

        // Mostrar notificación simple para cualquier mensaje
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Alerta360"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "Nueva notificación"

        showNotification(title, body)

        Log.d(TAG, "✅ FCM Message processing completed")
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "🔄 FCM Token refreshed!")
        Log.d(TAG, "📱 New token: $token")

        // Aquí podrías enviar el token al servidor
        // Por ahora solo lo loggeamos para debug
    }

    private fun showNotification(title: String, body: String) {
        Log.d(TAG, "📱 Showing notification: $title")

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Usar icono del sistema
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        Log.d(TAG, "✅ Notification displayed")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Incidentes Alerta360",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de nuevos incidentes y actualizaciones"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

            Log.d(TAG, "📢 Notification channel created: $CHANNEL_ID")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "💀 FCM Service DESTROYED!")
    }
}