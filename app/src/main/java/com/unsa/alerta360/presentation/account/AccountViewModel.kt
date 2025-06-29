package com.unsa.alerta360.presentation.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.unsa.alerta360.data.model.UserDto
import com.unsa.alerta360.domain.model.Account
import com.unsa.alerta360.domain.model.User
import com.unsa.alerta360.domain.repository.AccountRepository
import com.unsa.alerta360.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userData = MutableStateFlow(User())
    val userData: StateFlow<User> = _userData.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // Leer datos de backend
    fun loadAccount() {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                val userId = authRepository.getCurrentUser()?.uid
                if (userId !== null) {
                    val user = authRepository.getUserDetails(userId)
                    _userData.value = user
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // Guardar datos al backend
    fun guardarPerfil() {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null
            try {
                //authRepository.update(_userData.value._id.toString(), _userData.value)
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    // Los setters para los campos
    fun onNombresChange(value: String) {
        _userData.value = _userData.value.copy(first_name = value)
    }

    fun onApellidosChange(value: String) {
        _userData.value = _userData.value.copy(last_name = value)
    }

    fun onDireccionChange(value: String) {
        _userData.value = _userData.value.copy(dni = value)
    }

    fun onCelularChange(value: String) {
        _userData.value = _userData.value.copy(phone_number = value)
    }

    fun onDistritoChange(value: String) {
        _userData.value = _userData.value.copy(district = value)
    }
}
