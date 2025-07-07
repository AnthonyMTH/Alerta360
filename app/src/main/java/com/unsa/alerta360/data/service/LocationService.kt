package com.unsa.alerta360.data.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationService @Inject constructor(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient
) {

    suspend fun getCurrentLocation(): LatLng? {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null // Permisos no concedidos
        }

        return suspendCancellableCoroutine { continuation ->
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    continuation.resume(LatLng(location.latitude, location.longitude))
                } else {
                    continuation.resume(null)
                }
            }.addOnFailureListener { e ->
                continuation.resumeWithException(e)
            }
        }
    }
}
