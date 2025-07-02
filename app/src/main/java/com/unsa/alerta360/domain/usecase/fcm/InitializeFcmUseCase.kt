package com.unsa.alerta360.domain.usecase.fcm

import android.util.Log
import com.unsa.alerta360.domain.repository.FcmRepository
import javax.inject.Inject

class InitializeFcmUseCase @Inject constructor(
    private val fcmRepository: FcmRepository
) {
    suspend operator fun invoke(userId: String): Result<Unit> {
        return try {
            Log.d("InitializeFcm", "🚀 Starting FCM initialization for user: $userId")

            // Obtener token actual
            val token = fcmRepository.getCurrentToken()
            if (token != null) {
                Log.d("InitializeFcm", "📱 FCM Token obtained: ${token.take(20)}...")

                // Enviar token al servidor con el userId
                val tokenResult = fcmRepository.sendTokenToServer(userId, token)
                if (tokenResult.isSuccess) {
                    Log.d("InitializeFcm", "✅ Token sent to server successfully")
                } else {
                    Log.w("InitializeFcm", "⚠️ Failed to send token to server: ${tokenResult.exceptionOrNull()}")
                }

                // Suscribirse a tópicos globales con logs detallados
                Log.d("InitializeFcm", "🔔 Starting topic subscriptions...")

                val allIncidentsResult = fcmRepository.subscribeToTopic("all_incidents")
                if (allIncidentsResult.isSuccess) {
                    Log.d("InitializeFcm", "✅ Subscribed to: all_incidents")
                } else {
                    Log.e("InitializeFcm", "❌ Failed to subscribe to all_incidents")
                }

                val emergencyResult = fcmRepository.subscribeToTopic("emergency_alerts")
                if (emergencyResult.isSuccess) {
                    Log.d("InitializeFcm", "✅ Subscribed to: emergency_alerts")
                } else {
                    Log.e("InitializeFcm", "❌ Failed to subscribe to emergency_alerts")
                }

                Log.d("InitializeFcm", "🎯 FCM initialization completed successfully for user: $userId")
                Result.success(Unit)
            } else {
                Log.e("InitializeFcm", "❌ Could not get FCM token")
                Result.failure(Exception("Could not get FCM token"))
            }
        } catch (e: Exception) {
            Log.e("InitializeFcm", "💥 Error initializing FCM", e)
            Result.failure(e)
        }
    }
}