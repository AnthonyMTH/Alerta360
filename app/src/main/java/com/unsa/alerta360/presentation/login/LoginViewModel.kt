package com.unsa.alerta360.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unsa.alerta360.domain.model.Result // El Result de tu dominio
import com.unsa.alerta360.domain.usecase.auth.LoginUserUseCase
import com.unsa.alerta360.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor( // Inyecta el Caso de Uso
    private val loginUserUseCase: LoginUserUseCase
) : ViewModel() {

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _loginState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val loginState: StateFlow<UiState<Unit>> = _loginState.asStateFlow()

    // Para errores que se mostrarán en Snackbar
    private val _uiEvent = MutableStateFlow<LoginUiEvent?>(null)
    val uiEvent: StateFlow<LoginUiEvent?> = _uiEvent.asStateFlow()


    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onPasswordChange(newPassword: String) { _password.value = newPassword }

    fun loginUser() {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            // Las validaciones básicas podrían estar aquí o en el UseCase.
            // Si están en el UseCase, el Result.Error del UseCase las manejará.
            val result = loginUserUseCase(email.value.trim(), password.value)
            when (result) {
                is Result.Success -> {
                    _loginState.value = UiState.Success(Unit)
                }
                is Result.Error -> {
                    _loginState.value = UiState.Error(result.message ?: "Error desconocido")
                    _uiEvent.value = LoginUiEvent.ShowSnackbar(result.message ?: "Error desconocido")
                }
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = UiState.Idle
    }

    fun onEventConsumed() {
        _uiEvent.value = null
    }
}

// Para eventos de UI one-shot como Snackbars o navegación
sealed class LoginUiEvent {
    data class ShowSnackbar(val message: String) : LoginUiEvent()
    // object NavigateToHome : LoginUiEvent() // Si quisieras manejar navegación desde ViewModel
}