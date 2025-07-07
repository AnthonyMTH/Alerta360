package com.unsa.alerta360.presentation.map

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.presentation.common.UiState
import kotlinx.coroutines.flow.collectLatest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeatmapScreen(
    navController: NavController,
    viewModel: HeatmapViewModel = hiltViewModel()
) {
    val uiState by viewModel.incidents.collectAsState()
    val viewMode by viewModel.mapViewMode.collectAsState()

    val arequipa = LatLng(-16.409047, -71.537451)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(arequipa, 12f)
    }

    val locationPermissionRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    viewModel.requestMoveToUserLocation()
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    viewModel.requestMoveToUserLocation()
                }
                else -> {
                    // Permiso denegado, manejar según sea necesario
                }
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.navigateToIncidentDetail.collectLatest {
            kotlinx.coroutines.delay(100)
            navController.navigate("incident_detail/${it}")
        }
    }

    LaunchedEffect(Unit) {
        viewModel.moveToUserLocationEvent.collectLatest {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 15f),
                durationMs = 1000
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // GoogleMap se renderiza siempre, pero su contenido depende del UiState
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            // Contenido específico del mapa (TileOverlay, MarkerInfoWindow) solo si es UiState.Success
            if (uiState is UiState.Success) {
                val incidents = (uiState as UiState.Success).data
                when (viewMode) {
                    MapViewMode.HEATMAP -> {
                        val latLngList = incidents.mapNotNull { incident ->
                            val parts = incident.geolocation.split(",")
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
                        if (latLngList.isNotEmpty()) {
                            val heatmapTileProvider = HeatmapTileProvider.Builder()
                                .data(latLngList)
                                .build()
                            TileOverlay(tileProvider = heatmapTileProvider)
                        }
                    }
                    MapViewMode.MARKER -> {
                        incidents.forEach { incident ->
                            val parts = incident.geolocation.split(",")
                            if (parts.size == 2) {
                                val lat = parts[0].toDoubleOrNull()
                                val lng = parts[1].toDoubleOrNull()
                                if (lat != null && lng != null) {
                                    MarkerInfoWindow(
                                        state = MarkerState(position = LatLng(lat, lng)),
                                        onInfoWindowClick = {
                                            incident._id?.let { it1 ->
                                                viewModel.onIncidentInfoWindowClick(
                                                    it1
                                                )
                                            };
                                        },
                                        content = {
                                            IncidentInfoWindow(incident = incident)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Indicadores de carga y error (superpuestos al mapa)
        when (val state = uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            is UiState.Error -> {
                Text(
                    text = state.message,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.error
                )
            }
            else -> {}
        }

        // Switch (superpuesto al mapa)
        Switch(checked = viewMode == MapViewMode.MARKER, onCheckedChange = { isChecked ->
            viewModel.setMapViewMode(if (isChecked) MapViewMode.MARKER else MapViewMode.HEATMAP)
        }, modifier = Modifier
            .align(Alignment.TopEnd)
            .padding(top = 16.dp, end = 16.dp))

        // FloatingActionButton (superpuesto al mapa)
        FloatingActionButton(
            onClick = {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 16.dp, start = 16.dp)
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "Mi Ubicación")
        }
    }
}

@Composable
fun IncidentInfoWindow(
    incident: Incident
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        shadowElevation = 4.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = incident.title, fontWeight = FontWeight.Bold)
            Text(text = incident.incidentType, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
