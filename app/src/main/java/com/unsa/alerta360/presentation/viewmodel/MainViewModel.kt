package com.unsa.alerta360.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unsa.alerta360.domain.usecase.auth.GetCurrentUserUseCase
import com.unsa.alerta360.domain.usecase.fcm.InitializeFcmUseCase
import com.unsa.alerta360.domain.usecase.fcm.SubscribeToLocationUseCase
import com.unsa.alerta360.domain.usecase.fcm.UpdateFcmTokenUseCase
import com.unsa.alerta360.domain.usecase.fcm.UpdateNotificationPreferencesUseCase
import com.unsa.alerta360.domain.usecase.incident.GetAllIncidentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getAllIncidentsUseCase: GetAllIncidentsUseCase,
    private val initializeFcmUseCase: InitializeFcmUseCase,
    private val subscribeToLocationUseCase: SubscribeToLocationUseCase,
    private val updateFcmTokenUseCase: UpdateFcmTokenUseCase,
    private val updateNotificationPreferencesUseCase: UpdateNotificationPreferencesUseCase
) : ViewModel() {

    private val _fcmInitialized = MutableStateFlow(false)
    val fcmInitialized: StateFlow<Boolean> = _fcmInitialized.asStateFlow()

    private val _notificationPermissionGranted = MutableStateFlow(false)
    val notificationPermissionGranted: StateFlow<Boolean> = _notificationPermissionGranted.asStateFlow()

    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    companion object {
        private const val TAG = "MainViewModel"
    }

    fun setCurrentUserId(userId: String) {
        _currentUserId.value = userId
        // Inicializar FCM cuando tengamos el userId y los permisos
        if (_notificationPermissionGranted.value && !_fcmInitialized.value) {
            initializeFcm(userId)
        }
    }

    fun initializeFcm(userId: String) {
        viewModelScope.launch {
            try {
                val result = initializeFcmUseCase(userId)
                if (result.isSuccess) {
                    _fcmInitialized.value = true
                    Log.d(TAG, "FCM initialized successfully for user: $userId")
                } else {
                    Log.e(TAG, "Failed to initialize FCM: ${result.exceptionOrNull()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception initializing FCM", e)
            }
        }
    }

    fun subscribeToLocation(userId: String, district: String) {
        viewModelScope.launch {
            try {
                val result = subscribeToLocationUseCase(userId, district)
                if (result.isSuccess) {
                    Log.d(TAG, "Subscribed to location: $district for user: $userId")
                } else {
                    Log.e(TAG, "Failed to subscribe to location: $district")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception subscribing to location", e)
            }
        }
    }

    fun updateFcmToken(userId: String, token: String) {
        viewModelScope.launch {
            try {
                val result = updateFcmTokenUseCase(userId, token)
                if (result.isSuccess) {
                    Log.d(TAG, "FCM token updated successfully for user: $userId")
                } else {
                    Log.e(TAG, "Failed to update FCM token for user: $userId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception updating FCM token", e)
            }
        }
    }

    fun updateNotificationPreferences(
        userId: String,
        incidents: Boolean = true,
        emergencies: Boolean = true,
        location: Boolean = true
    ) {
        viewModelScope.launch {
            try {
                val result = updateNotificationPreferencesUseCase(userId, incidents, emergencies, location)
                if (result.isSuccess) {
                    Log.d(TAG, "Notification preferences updated for user: $userId")
                } else {
                    Log.e(TAG, "Failed to update notification preferences for user: $userId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception updating notification preferences", e)
            }
        }
    }

    fun setNotificationPermissionGranted(granted: Boolean) {
        _notificationPermissionGranted.value = granted
        if (granted && !_fcmInitialized.value) {
            _currentUserId.value?.let { userId ->
                initializeFcm(userId)
            }
        }
    }

    fun handleIncidentFromNotification(incidentId: String) {
        // Aquí puedes navegar a la pantalla del incidente específico
        Log.d(TAG, "Opening incident from notification: $incidentId")
    }
}