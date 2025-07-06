package com.unsa.alerta360.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.TileOverlay
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberTileOverlayState
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.unsa.alerta360.presentation.common.UiState
import com.unsa.alerta360.ui.theme.Alerta360Theme

@Composable
fun HeatmapScreen(
    modifier: Modifier = Modifier,
    viewModel: HeatmapViewModel = hiltViewModel()
) {
    val heatmapState by viewModel.heatmapData.collectAsState()

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        when (val state = heatmapState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Success -> {
                val heatmapData = state.data
                if (heatmapData.isNotEmpty()) {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(heatmapData.first(), 14f)
                    }
                    val tileOverlayState = rememberTileOverlayState()

                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        TileOverlay(
                            state = tileOverlayState,
                            tileProvider = HeatmapTileProvider.Builder()
                                .data(heatmapData)
                                .build()
                        )
                    }
                } else {
                    Text("No incident data available.")
                }
            }
            is UiState.Error -> {
                Text(state.message)
            }
            is UiState.Idle -> {
                // You can show a placeholder or nothing
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeatmapScreenPreview() {
    Alerta360Theme {
        HeatmapScreen()
    }
}
