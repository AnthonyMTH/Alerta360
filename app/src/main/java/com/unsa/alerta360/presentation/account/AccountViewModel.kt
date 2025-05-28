package com.unsa.alerta360.presentation.account

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor() : ViewModel() {

    private val _nombres = MutableStateFlow("")
    val nombres: StateFlow<String> = _nombres.asStateFlow()

    private val _apellidos = MutableStateFlow("")
    val apellidos: StateFlow<String> = _apellidos.asStateFlow()

    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion.asStateFlow()

    private val _celular = MutableStateFlow("")
    val celular: StateFlow<String> = _celular.asStateFlow()

    private val _distrito = MutableStateFlow("")
    val distrito: StateFlow<String> = _distrito.asStateFlow()

    private val _provincia = MutableStateFlow("")
    val provincia: StateFlow<String> = _provincia.asStateFlow()

    private val _departamento = MutableStateFlow("")
    val departamento: StateFlow<String> = _departamento.asStateFlow()

    fun onNombresChange(value: String) { _nombres.value = value }
    fun onApellidosChange(value: String) { _apellidos.value = value }
    fun onDireccionChange(value: String) { _direccion.value = value }
    fun onCelularChange(value: String) { _celular.value = value }
    fun onDistritoChange(value: String) { _distrito.value = value }
    fun onProvinciaChange(value: String) { _provincia.value = value }
    fun onDepartamentoChange(value: String) { _departamento.value = value }

    fun guardarPerfil() {
        println(
            "Perfil guardado:\n" +
                    "Nombres: ${_nombres.value}, Apellidos: ${_apellidos.value}, Dirección: ${_direccion.value},\n" +
                    "Distrito: ${_distrito.value}, Provincia: ${_provincia.value}, Departamento: ${_departamento.value},\n" +
                    "Celular: ${_celular.value}"
        )
    }
}
