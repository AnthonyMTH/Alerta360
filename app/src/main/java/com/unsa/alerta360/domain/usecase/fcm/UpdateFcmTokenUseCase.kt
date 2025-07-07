package com.unsa.alerta360.domain.usecase.fcm

import android.util.Log
import com.unsa.alerta360.domain.repository.FcmRepository
import javax.inject.Inject

class UpdateFcmTokenUseCase @Inject constructor(
    private val fcmRepository: FcmRepository
) {
    suspend operator fun invoke(userId: String, token: String): Result<Unit> {
        return try {
            Log.d("UpdateFcmToken", "🔄 Updating FCM token for user: $userId")
            Log.d("UpdateFcmToken", "📱 Token: ${token.take(20)}...")

            val result = fcmRepository.sendTokenToServer(userId, token)

            if (result.isSuccess) {
                Log.d("UpdateFcmToken", "✅ FCM token updated successfully")
            } else {
                Log.e("UpdateFcmToken", "❌ Failed to update FCM token")
            }

            result
        } catch (e: Exception) {
            Log.e("UpdateFcmToken", "💥 Exception updating FCM token", e)
            Result.failure(e)
        }
    }
}