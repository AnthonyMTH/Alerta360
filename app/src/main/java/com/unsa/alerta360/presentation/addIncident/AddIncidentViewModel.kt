package com.unsa.alerta360.presentation.addIncident

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.cloudinary.Configuration
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.usecase.incident.CreateIncidentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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


    private val _distrito = MutableStateFlow("")
    val distrito: StateFlow<String> = _distrito

    private val _imageUri = MutableStateFlow<Uri?>(null)
    val imageUri: StateFlow<Uri?> = _imageUri

    private val _description = MutableStateFlow("")
    val description: StateFlow<String> = _description

    private val _uiEvent = MutableStateFlow<AddIncidentEvent?>(null)
    val uiEvent: StateFlow<AddIncidentEvent?> = _uiEvent

    val tiposIncidente = listOf("Accidente", "Robo", "Otro")

    val distritos = listOf(
        "Alto Selva Alegre",
        "Cayma",
        "Cercado",
        "Cerro Colorado",
        "Characato",
        "Chiguata",
        "Jacobo Hunter",
        "José Luis Bustamante y Rivero",
        "Mariano Melgar",
        "Miraflores",
        "Mollebaya",
        "Paucarpata",
        "Polobaya",
        "Quequeña",
        "Sabandía",
        "Sachaca",
        "Socabaya",
        "Tiabaya",
        "Uchumayo",
        "Yanahuara",
        "Yarabamba",
        "Yura"
    )

    fun onTituloChange(value: String) { _titulo.value = value }
    fun onTipoIncidenteChange(value: String) { _tipoIncidente.value = value }
    fun onDireccionChange(value: String) { _direccion.value = value }

    fun onDistritoChange(value: String) { _distrito.value = value }
    fun onFileSelected(value: Uri) { _imageUri.value = value }
    fun onDescriptionChange(value: String) { _description.value = value }

    fun cancelar() {
        _uiEvent.value = AddIncidentEvent.NavigateBack
    }

    fun guardar(context: Context) {


        _uiEvent.value = AddIncidentEvent.Loading

        if (_titulo.value.isBlank() || _description.value.isBlank() || _tipoIncidente.value.isBlank() || _direccion.value.isBlank()) {
            _uiEvent.value = AddIncidentEvent.Error("Por favor, complete todos los campos requeridos.")
            return
        }


        viewModelScope.launch {

            try {
                val evidenceList = mutableListOf<String>()
                imageUri.value?.let { uri ->
                    val url = uploadImageToCloudinary(
                        context = context,
                        imageUri = uri,
                        cloudName = "hotelapp",
                        uploadPreset = "user_photos"
                    )
                    url?.let { evidenceList.add(it) }
                }

                val newIncident = Incident(
                    description = _description.value,
                    incidentType = _tipoIncidente.value,
                    ubication = _direccion.value,
                    geolocation = "-1233213, 123123",
                    evidence = evidenceList,
                    user_id = "682f18e1d21cf2679fa4fa81",
                    title = _titulo.value,
                    district = _distrito.value
                )

                val result = createIncidentUseCase(newIncident)
                if (result != null) {
                    // Emitir estado de "Éxito"

                    _uiEvent.value = AddIncidentEvent.Success("Incidente creado con éxito.")
                    _uiEvent.value = AddIncidentEvent.NavigateBack
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

private fun getFileFromUri(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
    inputStream?.use { input ->
        tempFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return tempFile
}
suspend fun uploadImageToCloudinary(
    context: Context,
    imageUri: Uri,
    cloudName: String,
    uploadPreset: String
): String? = withContext(Dispatchers.IO) {
    val file = getFileFromUri(context, imageUri)
    val cloudinary = Cloudinary("cloudinary://$cloudName@$cloudName")
    // Parámetros a pasar
    val params = mutableMapOf<String, Any>(
        "upload_preset" to uploadPreset,
        "unsigned" to true
    )
    val result = cloudinary.uploader().upload(file, params)
    result["secure_url"] as? String
}