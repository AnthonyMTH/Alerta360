package com.unsa.alerta360.presentation.addIncident

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class AddIncidentEvent {
    object NavigateBack : AddIncidentEvent()
}

@HiltViewModel
class AddIncidentViewModel @Inject constructor() : ViewModel() {

    private val _titulo = MutableStateFlow("")
    val titulo: StateFlow<String> = _titulo

    private val _tipoIncidente = MutableStateFlow("")
    val tipoIncidente: StateFlow<String> = _tipoIncidente

    private val _direccion = MutableStateFlow("")
    val direccion: StateFlow<String> = _direccion

    private val _departamento = MutableStateFlow("")
    val departamento: StateFlow<String> = _departamento

    private val _provincia = MutableStateFlow("")
    val provincia: StateFlow<String> = _provincia

    private val _distrito = MutableStateFlow("")
    val distrito: StateFlow<String> = _distrito

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _uiEvent = MutableStateFlow<AddIncidentEvent?>(null)
    val uiEvent: StateFlow<AddIncidentEvent?> = _uiEvent

    val tiposIncidente = listOf("Accidente", "Robo", "Otro")
    val departamentos = listOf("Lima", "Cusco", "Arequipa")
    val provincias = listOf("Lima", "Urubamba", "Caylloma")
    val distritos = listOf("Miraflores", "San Isidro", "Surco")

    fun onTituloChange(value: String) { _titulo.value = value }
    fun onTipoIncidenteChange(value: String) { _tipoIncidente.value = value }
    fun onDireccionChange(value: String) { _direccion.value = value }
    fun onDepartamentoChange(value: String) { _departamento.value = value }
    fun onProvinciaChange(value: String) { _provincia.value = value }
    fun onDistritoChange(value: String) { _distrito.value = value }
    fun onFileSelected(value: Uri) { _imageUri.value = value }
    fun onDescriptionChange(value: String) { _description.value = value }

    fun cancelar() {
        _uiEvent.value = AddIncidentEvent.NavigateBack
    }

    fun guardar() {
        Log.d("AddIncidentViewModel", "Título: ${_titulo.value}")
        Log.d("AddIncidentViewModel", "Tipo Incidente: ${_tipoIncidente.value}")
        Log.d("AddIncidentViewModel", "Dirección: ${_direccion.value}")
        Log.d("AddIncidentViewModel", "Departamento: ${_departamento.value}")
        Log.d("AddIncidentViewModel", "Provincia: ${_provincia.value}")
        Log.d("AddIncidentViewModel", "Distrito: ${_distrito.value}")
        Log.d("AddIncidentViewModel", "Descripción: ${_description.value}")
        Log.d("AddIncidentViewModel", "Imagen URI: ${_imageUri.value}")
    }
    fun clearEvent() {
        _uiEvent.value = null
    }
}