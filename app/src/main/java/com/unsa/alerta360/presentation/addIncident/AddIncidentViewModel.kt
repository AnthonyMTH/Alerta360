package com.unsa.alerta360.presentation.addIncident

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class AddIncidentViewModel : ViewModel() {
    var titulo by mutableStateOf("")
        private set

    var tipoIncidente by mutableStateOf("")
        private set

    var direccion by mutableStateOf("")
        private set

    var departamento by mutableStateOf("")
        private set

    var provincia by mutableStateOf("")
        private set

    var distrito by mutableStateOf("")
        private set
    var imageUri by mutableStateOf<Uri?>(null)

    val tiposIncidente = listOf("Accidente", "Robo", "Otro")
    val departamentos = listOf("Lima", "Cusco", "Arequipa")
    val provincias = listOf("Lima", "Urubamba", "Caylloma")
    val distritos = listOf("Miraflores", "San Isidro", "Surco")

    fun onTituloChange(value: String) { titulo = value }
    fun onTipoIncidenteChange(value: String) { tipoIncidente = value }
    fun onDireccionChange(value: String) { direccion = value }
    fun onDepartamentoChange(value: String) { departamento = value }
    fun onProvinciaChange(value: String) { provincia = value }
    fun onDistritoChange(value: String) { distrito = value }
    fun onFileSelected(value: Uri) {imageUri = value}

    fun cancelar() {

    }
}
