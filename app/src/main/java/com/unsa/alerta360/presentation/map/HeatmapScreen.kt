package com.unsa.alerta360.presentation.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.unsa.alerta360.ui.theme.Alerta360Theme

@Composable
fun HeatmapScreen(
    modifier: Modifier = Modifier,
    viewModel: HeatmapViewModel = hiltViewModel()
) {
    val heatmapData = remember {
        listOf(
            LatLng(-16.409047, -71.537451),
            LatLng(-16.4091, -71.5375),
            LatLng(-16.4092, -71.5376),
            LatLng(-16.4089, -71.5373),
            LatLng(-16.4088, -71.5372),
            LatLng(-16.405, -71.535),
            LatLng(-16.4055, -71.5355),
            LatLng(-16.406, -71.536)
        )
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(heatmapData.first(), 14f)
    }

    val tileOverlayState = rememberTileOverlayState()

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        TileOverlay(
            state = tileOverlayState,
            tileProvider = HeatmapTileProvider.Builder()
                .data(heatmapData)
                .build()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeatmapScreenPreview() {
    Alerta360Theme {
        HeatmapScreen()
    }
}
