package com.unsa.alerta360.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unsa.alerta360.domain.usecase.auth.GetCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashDestination {
    object Initial : SplashDestination()
    object Home : SplashDestination()
    object Undetermined : SplashDestination() // Estado inicial mientras se verifica
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination>(SplashDestination.Undetermined)
    val destination: StateFlow<SplashDestination> = _destination.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            // Añadir un pequeño delay si quieres que el splash se vea un momento
            // kotlinx.coroutines.delay(1000)
            val currentUser = getCurrentUserUseCase()
            if (currentUser != null) {
                _destination.value = SplashDestination.Home
            } else {
                _destination.value = SplashDestination.Initial
            }
        }
    }
}