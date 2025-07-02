package com.unsa.alerta360.domain.usecase.fcm

import android.util.Log
import com.unsa.alerta360.domain.repository.FcmRepository
import javax.inject.Inject

class SubscribeToLocationUseCase @Inject constructor(
    private val fcmRepository: FcmRepository
) {
    suspend operator fun invoke(userId: String, district: String): Result<Unit> {
        return try {
            Log.d("SubscribeLocation", "🌍 Subscribing user $userId to location: $district")

            val result = fcmRepository.subscribeToLocation(userId, district)

            if (result.isSuccess) {
                Log.d("SubscribeLocation", "✅ Successfully subscribed to location: $district")
            } else {
                Log.e("SubscribeLocation", "❌ Failed to subscribe to location: $district")
            }

            result
        } catch (e: Exception) {
            Log.e("SubscribeLocation", "💥 Exception subscribing to location", e)
            Result.failure(e)
        }
    }
}