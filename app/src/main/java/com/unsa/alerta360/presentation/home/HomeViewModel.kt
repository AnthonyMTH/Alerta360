package com.unsa.alerta360.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.usecase.incident.GetAllIncidentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val incidents: List<Incident>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

sealed class TabSelection {
    object Todos : TabSelection()
    object MiHistorial : TabSelection()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllIncidentsUseCase: GetAllIncidentsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _selectedTab = MutableStateFlow<TabSelection>(TabSelection.Todos)
    val selectedTab: StateFlow<TabSelection> = _selectedTab

    private val _allIncidents = MutableStateFlow<List<Incident>>(emptyList())

    init {
        loadIncidents()
    }

    fun selectTab(tab: TabSelection) {
        _selectedTab.value = tab
        when (tab) {
            is TabSelection.Todos -> {
                _uiState.value = HomeUiState.Success(_allIncidents.value)
            }
            is TabSelection.MiHistorial -> {
                // Por ahora mostrar todos los incidentes, después se filtrará por usuario
                _uiState.value = HomeUiState.Success(_allIncidents.value)
            }
        }
    }

    private fun loadIncidents() {
        _uiState.value = HomeUiState.Loading
        viewModelScope.launch {
            try {
                val incidents = getAllIncidentsUseCase()
                _allIncidents.value = incidents
                
                // Mostrar todos los incidentes cuando se carguen inicialmente
                when (_selectedTab.value) {
                    is TabSelection.Todos -> {
                        _uiState.value = HomeUiState.Success(incidents)
                    }
                    is TabSelection.MiHistorial -> {
                        // Por ahora mostrar todos, después filtrar por usuario
                        _uiState.value = HomeUiState.Success(incidents)
                    }
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("JsonReader") == true -> 
                        "Error de conexión: El servidor no está respondiendo correctamente"
                    e.message?.contains("timeout") == true -> 
                        "Tiempo de espera agotado: Verifica tu conexión a internet"
                    e.message?.contains("Unable to resolve host") == true -> 
                        "Sin conexión a internet: Verifica tu conexión de red"
                    else -> "Error al cargar incidentes: ${e.message}"
                }
                _uiState.value = HomeUiState.Error(errorMessage)
            }
        }
    }

    fun refreshIncidents() {
        loadIncidents()
    }
}