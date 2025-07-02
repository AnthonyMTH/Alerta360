package com.unsa.alerta360.domain.usecase.fcm

import android.util.Log
import com.unsa.alerta360.domain.repository.FcmRepository
import javax.inject.Inject

class UpdateNotificationPreferencesUseCase @Inject constructor(
    private val fcmRepository: FcmRepository
) {
    suspend operator fun invoke(
        userId: String,
        incidents: Boolean = true,
        emergencies: Boolean = true,
        location: Boolean = true
    ): Result<Unit> {
        return try {
            Log.d("UpdateNotifPrefs", "⚙️ Updating notification preferences for user: $userId")
            Log.d("UpdateNotifPrefs", "📋 Incidents: $incidents, Emergencies: $emergencies, Location: $location")

            val result = fcmRepository.updateNotificationPreferences(userId, incidents, emergencies, location)

            if (result.isSuccess) {
                Log.d("UpdateNotifPrefs", "✅ Notification preferences updated successfully")
            } else {
                Log.e("UpdateNotifPrefs", "❌ Failed to update notification preferences")
            }

            result
        } catch (e: Exception) {
            Log.e("UpdateNotifPrefs", "💥 Exception updating notification preferences", e)
            Result.failure(e)
        }
    }
}