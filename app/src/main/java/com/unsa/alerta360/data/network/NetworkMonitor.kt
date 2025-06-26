package com.unsa.alerta360.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.unsa.alerta360.data.worker.SyncIncidentsWorker

object NetworkMonitor {

    fun hasNetwork(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(net) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun startListening(context: Context) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerNetworkCallback(
            NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build(),
            object: ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    scheduleSync(context)
                }
            }
        )
    }

    private fun scheduleSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val work = OneTimeWorkRequestBuilder<SyncIncidentsWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                "sync_incidents",
                ExistingWorkPolicy.KEEP,
                work
            )
    }
}