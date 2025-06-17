package com.unsa.alerta360.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.model.IncidentWithUser
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.usecase.incident.GetAllIncidentsUseCase
import com.unsa.alerta360.domain.usecase.user.GetUserDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val incidents: List<IncidentWithUser>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

sealed class TabSelection {
    object Todos : TabSelection()
    object MiHistorial : TabSelection()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllIncidentsUseCase: GetAllIncidentsUseCase,
    private val getUserDetailsUseCase: GetUserDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _selectedTab = MutableStateFlow<TabSelection>(TabSelection.Todos)
    val selectedTab: StateFlow<TabSelection> = _selectedTab

    private val _allIncidents = MutableStateFlow<List<IncidentWithUser>>(emptyList())

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
                
                // Cargar información del usuario para cada incidente
                val incidentsWithUser = incidents.map { incident ->
                    val userResult = getUserDetailsUseCase(incident.user_id)
                    val userName = when (userResult) {
                        is Result.Success -> {
                            val userData = userResult.data
                            if (userData != null) {
                                "${userData.nombres} ${userData.apellidos}".trim()
                            } else {
                                "Usuario desconocido"
                            }
                        }
                        is Result.Error -> "Usuario desconocido"
                    }
                    IncidentWithUser(incident, userName)
                }
                
                _allIncidents.value = incidentsWithUser
                
                // Mostrar todos los incidentes cuando se carguen inicialmente
                when (_selectedTab.value) {
                    is TabSelection.Todos -> {
                        _uiState.value = HomeUiState.Success(incidentsWithUser)
                    }
                    is TabSelection.MiHistorial -> {
                        // Por ahora mostrar todos, después filtrar por usuario
                        _uiState.value = HomeUiState.Success(incidentsWithUser)
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