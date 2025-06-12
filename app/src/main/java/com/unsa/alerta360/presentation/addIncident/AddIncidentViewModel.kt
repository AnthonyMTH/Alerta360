package com.unsa.alerta360.presentation.addIncident

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.usecase.incident.CreateIncidentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AddIncidentEvent {
    object NavigateBack : AddIncidentEvent()
    object Loading : AddIncidentEvent()
    data class Success(val message: String) : AddIncidentEvent()
    data class Error(val message: String) : AddIncidentEvent()
}

@HiltViewModel
class AddIncidentViewModel @Inject constructor(private val createIncidentUseCase: CreateIncidentUseCase) : ViewModel() {

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


        _uiEvent.value = AddIncidentEvent.Loading

        if (_titulo.value.isBlank() || _description.value.isBlank() || _tipoIncidente.value.isBlank() || _direccion.value.isBlank()) {
            _uiEvent.value = AddIncidentEvent.Error("Por favor, complete todos los campos requeridos.")
            return
        }


        viewModelScope.launch {
            val newIncident = Incident(
                description = _description.value,
                incidentType = _tipoIncidente.value,
                ubication = _direccion.value,
                geolocation = "-1233213, 123123", // Se calculará luego con FusedLocationProviderClient
                evidence = listOf("https://elbuho.pe/wp-content/uploads/2024/05/Noticiero-17-de-mayo-2024.jpeg",
                    "https://i.ytimg.com/vi/VCOTrE-1j-U/maxresdefault.jpg"), // datos mock
                user_id = "682f18e1d21cf2679fa4fa81", // ID luego se integrará con usuario autenticado
                title = _titulo.value
            )
            try {
                val result = createIncidentUseCase(newIncident)
                if (result != null) {
                    // Emitir estado de "Éxito"
                    //_uiEvent.value = AddIncidentEvent.NavigateBack
                    _uiEvent.value = AddIncidentEvent.Success("Incidente creado con éxito.")

                } else {
                    // Emitir estado de "Error" en caso de fallo
                    _uiEvent.value = AddIncidentEvent.Error("Error al crear incidente.")
                }
            } catch (e: Exception) {
                // Capturar excepciones y emitir estado de error
                _uiEvent.value = AddIncidentEvent.Error("Excepción al crear incidente: ${e.message}")
            }
        }

    }
    fun clearEvent() {
        _uiEvent.value = null
    }
}