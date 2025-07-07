package com.unsa.alerta360.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
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
    openDrawer: () -> Unit,
    navController: NavController,
    viewModel: HeatmapViewModel = hiltViewModel()
) {
    val uiState by viewModel.incidents.collectAsState()
    val viewMode by viewModel.mapViewMode.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigateToIncidentDetail.collectLatest {
            kotlinx.coroutines.delay(100) // Pequeño retraso para evitar ANR
            navController.navigate("incident_detail/${it}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mapa de Calor") },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UiState.Success -> {
                    val incidents = state.data
                    val arequipa = LatLng(-16.409047, -71.537451)
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(arequipa, 12f)
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
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
                                                    }
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
                    Switch(checked = viewMode == MapViewMode.MARKER, onCheckedChange = { isChecked ->
                        viewModel.setMapViewMode(if (isChecked) MapViewMode.MARKER else MapViewMode.HEATMAP)
                    }, modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp))
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
            Text(text = incident.description, maxLines = 2, overflow = TextOverflow.Ellipsis)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* Clic manejado por onInfoWindowClick */ }) {
                Text("Ver Detalles")
            }
        }
    }
}
