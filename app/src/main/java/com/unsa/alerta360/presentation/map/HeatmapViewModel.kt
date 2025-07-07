package com.unsa.alerta360.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.usecase.incident.GetAllIncidentsUseCase
import com.unsa.alerta360.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class MapViewMode {
    HEATMAP,
    MARKER
}

@HiltViewModel
class HeatmapViewModel @Inject constructor(
    private val getAllIncidentsUseCase: GetAllIncidentsUseCase
) : ViewModel() {

    private val _incidents = MutableStateFlow<UiState<List<Incident>>>(UiState.Idle)
    val incidents: StateFlow<UiState<List<Incident>>> = _incidents

    private val _mapViewMode = MutableStateFlow(MapViewMode.HEATMAP)
    val mapViewMode: StateFlow<MapViewMode> = _mapViewMode.asStateFlow()

    private val _navigateToIncidentDetail = MutableSharedFlow<String>()
    val navigateToIncidentDetail: SharedFlow<String> = _navigateToIncidentDetail.asSharedFlow()

    init {
        loadIncidents()
    }

    fun setMapViewMode(mode: MapViewMode) {
        _mapViewMode.value = mode
    }

    fun onIncidentInfoWindowClick(incidentId: String) {
        viewModelScope.launch {
            _navigateToIncidentDetail.emit(incidentId)
        }
    }

    private fun loadIncidents() {
        viewModelScope.launch {
            _incidents.value = UiState.Loading
            getAllIncidentsUseCase()
                .catch { e ->
                    _incidents.value = UiState.Error(e.message ?: "An unexpected error occurred")
                }
                .collect { incidents ->
                    _incidents.value = UiState.Success(incidents)
                }
        }
    }
}
