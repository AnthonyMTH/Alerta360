package com.unsa.alerta360.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val PREFS_NAME = "alerta360_prefs"
        private const val USER_ID_KEY = "user_id"
        private const val PENDING_FCM_TOKEN_KEY = "pending_fcm_token"
    }

    private val sharedPrefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveUserId(userId: String) {
        sharedPrefs.edit().putString(USER_ID_KEY, userId).apply()
    }

    fun getUserId(): String? {
        return sharedPrefs.getString(USER_ID_KEY, null)
    }

    fun clearUserId() {
        sharedPrefs.edit().remove(USER_ID_KEY).apply()
    }

    fun savePendingFcmToken(token: String) {
        sharedPrefs.edit().putString(PENDING_FCM_TOKEN_KEY, token).apply()
    }

    fun getPendingFcmToken(): String? {
        return sharedPrefs.getString(PENDING_FCM_TOKEN_KEY, null)
    }

    fun clearPendingFcmToken() {
        sharedPrefs.edit().remove(PENDING_FCM_TOKEN_KEY).apply()
    }
}