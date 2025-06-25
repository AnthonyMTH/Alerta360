package com.unsa.alerta360.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unsa.alerta360.data.repository.Resource
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.model.IncidentWithUser
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.usecase.account.GetAccountDetailsUseCase
import com.unsa.alerta360.domain.usecase.incident.GetAllIncidentsUseCase
import com.unsa.alerta360.domain.usecase.incident.ObserveAllIncidentsUseCase
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
    private val observeAllIncidentsUseCase: ObserveAllIncidentsUseCase,
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState

    private val _selectedTab = MutableStateFlow<TabSelection>(TabSelection.Todos)
    val selectedTab: StateFlow<TabSelection> = _selectedTab

    private val _allIncidents = MutableStateFlow<List<IncidentWithUser>>(emptyList())

    init {
        observeIncidents()
    }

    private fun observeIncidents() {
        viewModelScope.launch {
            observeAllIncidentsUseCase().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.value = HomeUiState.Loading
                    }

                    is Resource.Success -> {
                        val enriched = resource.data.orEmpty().map { incident ->
                            val userResult = getAccountDetailsUseCase(incident.user_id)
                            val userName = when (userResult) {
                                is Result.Success -> {
                                    val user = userResult.data
                                    if (user != null) "${user.first_name} ${user.last_name}".trim()
                                    else "Usuario desconocido"
                                }

                                is Result.Error -> "Usuario desconocido"
                            }
                            IncidentWithUser(incident, userName)
                        }

                        _allIncidents.value = enriched
                        updateUiByTab()
                    }

                    is Resource.Error -> {
                        val enriched = resource.data.orEmpty().map { incident ->
                            val userResult = getAccountDetailsUseCase(incident.user_id)
                            val userName = when (userResult) {
                                is Result.Success -> {
                                    val user = userResult.data
                                    if (user != null) "${user.first_name} ${user.last_name}".trim()
                                    else "Usuario desconocido"
                                }

                                is Result.Error -> "Usuario desconocido"
                            }
                            IncidentWithUser(incident, userName)
                        }

                        _allIncidents.value = enriched
                        _uiState.value = HomeUiState.Error(resource.message ?: "Error desconocido")
                    }
                }
            }
        }
    }

    private fun updateUiByTab() {
        when (_selectedTab.value) {
            is TabSelection.Todos -> {
                _uiState.value = HomeUiState.Success(_allIncidents.value)
            }

            is TabSelection.MiHistorial -> {
                // Implementa lógica de filtrado por usuario si deseas
                _uiState.value = HomeUiState.Success(_allIncidents.value)
            }
        }
    }

    fun selectTab(tab: TabSelection) {
        _selectedTab.value = tab
        updateUiByTab()
    }

    fun refreshIncidents() {
        // Ya no necesitas forzar un refresh si estás usando NetworkBoundResource;
        // si deseas forzar un nuevo fetch, deberías hacer que `shouldFetch()` devuelva true (puedes agregar lógica extra).
    }
}
