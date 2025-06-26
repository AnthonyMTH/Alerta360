package com.unsa.alerta360.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unsa.alerta360.domain.model.IncidentWithUser
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.usecase.account.GetAccountDetailsUseCase
import com.unsa.alerta360.domain.usecase.incident.GetAllIncidentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow<TabSelection>(TabSelection.Todos)
    val selectedTab: StateFlow<TabSelection> = _selectedTab.asStateFlow()

    private val _allIncidents = MutableStateFlow<List<IncidentWithUser>>(emptyList())

    init {
        observeIncidents()
    }

    fun selectTab(tab: TabSelection) {
        _selectedTab.value = tab
        applyFilter()
    }

    fun refreshIncidents() {
        observeIncidents() // esto re-suscribe y dispara de nuevo el fetch (onStart)
    }

    private fun observeIncidents() {
        viewModelScope.launch {
            getAllIncidentsUseCase()
                .onStart { _uiState.value = HomeUiState.Loading }
                .catch { e ->
                    _uiState.value = HomeUiState.Error(e.localizedMessage ?: "Error desconocido")
                }
                .collect { incidents ->
                    val withUser = incidents.map { incident ->
                        val userResult = getAccountDetailsUseCase(incident.user_id)
                        val name = when (userResult) {
                            is Result.Success ->
                                userResult.data
                                    ?.let { "${it.first_name} ${it.last_name}".trim() }
                                    .takeUnless { it.isNullOrBlank() }
                                    ?: "Usuario desconocido"
                            is Result.Error -> "Usuario desconocido"
                        }
                        IncidentWithUser(incident, name)
                    }


                    _allIncidents.value = withUser

                    // Emite el estado filtrado según la pestaña actual
                    applyFilter()
                }
        }
    }

    private fun applyFilter() {
        val lista = when (_selectedTab.value) {
            is TabSelection.MiHistorial ->
                _allIncidents.value.filter { /* it.incident.user_id == miId */ true }
            TabSelection.Todos -> _allIncidents.value
        }
        _uiState.value = HomeUiState.Success(lista)
    }
}
