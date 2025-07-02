package com.unsa.alerta360.domain.repository

interface FcmRepository {
    suspend fun sendTokenToServer(userId: String, token: String): Result<Unit>
    suspend fun updateNotificationPreferences(userId: String, incidents: Boolean, emergencies: Boolean, location: Boolean): Result<Unit>
    suspend fun subscribeToLocation(userId: String, district: String): Result<Unit>
    suspend fun getCurrentToken(): String?
    suspend fun subscribeToTopic(topic: String): Result<Unit>
    suspend fun unsubscribeFromTopic(topic: String): Result<Unit>
}