package com.unsa.alerta360.presentation.incident

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import com.unsa.alerta360.data.local.Incident

class IncidentViewModel : ViewModel() {

    private val _incidents = mutableStateListOf<Incident>()
    private var currentIndex = mutableStateOf(0)
    val currentIncident: State<Incident?> = derivedStateOf {
        if (_incidents.isNotEmpty()) _incidents[currentIndex.value] else null
    }

    init {
        loadMockIncidents()
    }

    private fun loadMockIncidents() {
        _incidents.addAll(
            listOf(
                Incident(
                    title = "Robo a mano armada",
                    username = "Juan Pérez",
                    location = "Arequipa, Arequipa",
                    address = "Av. Ejemplo 123",
                    description = "Un incidente ocurrió donde se produjo un robo a mano armada.",
                    district = "Cercado",
                    type = "Robo"
                ),
                Incident(
                    title = "Asalto en moto",
                    username = "Ana Torres",
                    location = "Lima, Lima",
                    address = "Jirón Libertad 456",
                    description = "Dos sujetos en moto interceptaron a un transeúnte.",
                    district = "Miraflores",
                    type = "Asalto"
                ),
                Incident(
                    title = "Hurto en comercio",
                    username = "Carlos Gómez",
                    location = "Cusco, Cusco",
                    address = "Calle Comercio 789",
                    description = "Se reportó hurto dentro de una tienda local.",
                    district = "Centro Histórico",
                    type = "Hurto"
                )
            )
        )
    }

    fun nextIncident() {
        if (_incidents.isNotEmpty()) {
            currentIndex.value = (currentIndex.value + 1) % _incidents.size
        }
    }
}