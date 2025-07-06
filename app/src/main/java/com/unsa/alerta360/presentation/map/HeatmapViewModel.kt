package com.unsa.alerta360.presentation.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.unsa.alerta360.domain.usecase.incident.GetAllIncidentsUseCase
import com.unsa.alerta360.presentation.common.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HeatmapViewModel @Inject constructor(
    private val getAllIncidentsUseCase: GetAllIncidentsUseCase
) : ViewModel() {

    private val _heatmapData = MutableStateFlow<UiState<List<LatLng>>>(UiState.Idle)
    val heatmapData: StateFlow<UiState<List<LatLng>>> = _heatmapData

    init {
        loadIncidents()
    }

    private fun loadIncidents() {
        viewModelScope.launch {
            _heatmapData.value = UiState.Loading
            getAllIncidentsUseCase()
                .catch { e ->
                    _heatmapData.value = UiState.Error(e.message ?: "An unexpected error occurred")
                }
                .collect { incidents ->
                    val latLngList = incidents.mapNotNull {
                        val parts = it.geolocation.split(",")
                        if (parts.size == 2) {
                            val lat = parts[0].toDoubleOrNull()
                            val lng = parts[1].toDoubleOrNull()
                            if (lat != null && lng != null) {
                                LatLng(lat, lng)
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                    }
                    _heatmapData.value = UiState.Success(latLngList)
                }
        }
    }
}

