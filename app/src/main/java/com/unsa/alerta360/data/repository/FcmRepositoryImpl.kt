package com.unsa.alerta360.data.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.unsa.alerta360.data.network.FcmApiService
import com.unsa.alerta360.domain.repository.FcmRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmRepositoryImpl @Inject constructor(
    private val fcmApiService: FcmApiService,
    private val firebaseMessaging: FirebaseMessaging
) : FcmRepository {

    companion object {
        private const val TAG = "FcmRepository"
    }

    override suspend fun sendTokenToServer(userId: String, token: String): Result<Unit> {
        return try {
            val response = fcmApiService.updateFcmToken(userId, mapOf("fcm_token" to token))
            if (response.isSuccessful) {
                Log.d(TAG, "Token sent successfully to server for user: $userId")
                Result.success(Unit)
            } else {
                Log.e(TAG, "Failed to send token: ${response.code()}")
                Result.failure(Exception("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception sending token", e)
            Result.failure(e)
        }
    }

    override suspend fun updateNotificationPreferences(
        userId: String,
        incidents: Boolean,
        emergencies: Boolean,
        location: Boolean
    ): Result<Unit> {
        return try {
            val preferences = mapOf(
                "incidents" to incidents,
                "emergencies" to emergencies,
                "location" to location
            )
            val response = fcmApiService.updateNotificationPreferences(userId, preferences)
            if (response.isSuccessful) {
                Log.d(TAG, "Notification preferences updated for user: $userId")
                Result.success(Unit)
            } else {
                Log.e(TAG, "Failed to update preferences: ${response.code()}")
                Result.failure(Exception("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception updating preferences", e)
            Result.failure(e)
        }
    }

    override suspend fun subscribeToLocation(userId: String, district: String): Result<Unit> {
        return try {
            // Suscribirse localmente al tópico
            val locationTopic = "location_${district.lowercase().replace(" ", "_")}"
            subscribeToTopic(locationTopic)

            // Notificar al servidor
            val response = fcmApiService.subscribeToLocation(userId, mapOf("district" to district))
            if (response.isSuccessful) {
                Log.d(TAG, "Subscribed to location: $district for user: $userId")
                Result.success(Unit)
            } else {
                Log.e(TAG, "Failed to subscribe to location: ${response.code()}")
                Result.failure(Exception("Server error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception subscribing to location", e)
            Result.failure(e)
        }
    }

    override suspend fun getCurrentToken(): String? {
        return try {
            firebaseMessaging.token.await()
        } catch (e: Exception) {
            Log.e(TAG, "Error getting FCM token", e)
            null
        }
    }

    override suspend fun subscribeToTopic(topic: String): Result<Unit> {
        return try {
            firebaseMessaging.subscribeToTopic(topic).await()
            Log.d(TAG, "✅ Successfully subscribed to topic: $topic")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error subscribing to topic: $topic", e)
            Result.failure(e)
        }
    }

    override suspend fun unsubscribeFromTopic(topic: String): Result<Unit> {
        return try {
            firebaseMessaging.unsubscribeFromTopic(topic).await()
            Log.d(TAG, "✅ Successfully unsubscribed from topic: $topic")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error unsubscribing from topic: $topic", e)
            Result.failure(e)
        }
    }

    // Nuevo método para suscribirse a múltiples tópicos con logs detallados
    suspend fun subscribeToAllTopics(userId: String): Result<Unit> {
        return try {
            val topics = listOf(
                "all_incidents",
                "emergency_alerts"
            )

            Log.d(TAG, "🔔 Starting subscription to all topics for user: $userId")

            for (topic in topics) {
                try {
                    firebaseMessaging.subscribeToTopic(topic).await()
                    Log.d(TAG, "✅ Subscribed to: $topic")
                } catch (e: Exception) {
                    Log.e(TAG, "❌ Failed to subscribe to: $topic", e)
                }
            }

            Log.d(TAG, "🎯 Completed subscription process")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "💥 Error in bulk subscription", e)
            Result.failure(e)
        }
    }
}