package com.unsa.alerta360.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unsa.alerta360.domain.model.Result // El Result de tu dominio
import com.unsa.alerta360.domain.usecase.auth.RegisterUserInput
import com.unsa.alerta360.domain.usecase.auth.RegisterUserUseCase
import com.unsa.alerta360.presentation.common.UiState // Reutiliza UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUserUseCase: RegisterUserUseCase
) : ViewModel() {

    private val _nombres = MutableStateFlow("")
    val nombres: StateFlow<String> = _nombres.asStateFlow()
    private val _apellidos = MutableStateFlow("")
    val apellidos: StateFlow<String> = _apellidos.asStateFlow()
    private val _dni = MutableStateFlow("")
    val dni: StateFlow<String> = _dni.asStateFlow()
    private val _celular = MutableStateFlow("")
    val celular: StateFlow<String> = _celular.asStateFlow()
    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo.asStateFlow()
    private val _contrasena = MutableStateFlow("")
    val contrasena: StateFlow<String> = _contrasena.asStateFlow()
    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion.asStateFlow()
    private val _distrito = MutableStateFlow("")
    val distrito: StateFlow<String> = _distrito.asStateFlow()

    private val _distritoExpanded = MutableStateFlow(false)
    val distritoExpanded: StateFlow<Boolean> = _distritoExpanded.asStateFlow()

    private val _registrationState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val registrationState: StateFlow<UiState<Unit>> = _registrationState.asStateFlow()

    private val _uiEvent = MutableStateFlow<RegisterUiEvent?>(null)
    val uiEvent: StateFlow<RegisterUiEvent?> = _uiEvent.asStateFlow()

    // --- Funciones onValueChange ---
    fun onNombresChange(value: String) { _nombres.value = value }
    fun onApellidosChange(value: String) { _apellidos.value = value }
    fun onDniChange(value: String) { _dni.value = value }
    fun onCelularChange(value: String) { _celular.value = value }
    fun onCorreoChange(value: String) { _correo.value = value }
    fun onContrasenaChange(value: String) { _contrasena.value = value }
    fun onDireccionChange(value: String) { _direccion.value = value }
    fun onDistritoChange(value: String) { _distrito.value = value }
    fun onDistritoExpandedChange(expanded: Boolean) { _distritoExpanded.value = expanded }

    fun registerUser() {
        viewModelScope.launch {
            _registrationState.value = UiState.Loading
            val input = RegisterUserInput(
                email = correo.value,
                contrasena = contrasena.value,
                nombres = nombres.value,
                apellidos = apellidos.value,
                dni = dni.value,
                celular = celular.value,
                direccion = direccion.value,
                distrito = distrito.value
            )
            when (val result = registerUserUseCase(input)) {
                is Result.Success -> {
                    _registrationState.value = UiState.Success(Unit)
                    _uiEvent.value = RegisterUiEvent.ShowSnackbar("¡Registro exitoso!")
                }
                is Result.Error -> {
                    _registrationState.value = UiState.Error(result.message ?: "Error desconocido")
                    _uiEvent.value = RegisterUiEvent.ShowSnackbar(result.message ?: "Error en el registro.")
                }
            }
        }
    }

    fun resetRegistrationState() {
        _registrationState.value = UiState.Idle
    }

    fun onEventConsumed() {
        _uiEvent.value = null
    }
}

sealed class RegisterUiEvent {
    data class ShowSnackbar(val message: String) : RegisterUiEvent()
}