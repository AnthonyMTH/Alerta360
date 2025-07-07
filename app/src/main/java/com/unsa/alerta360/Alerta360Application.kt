package com.unsa.alerta360

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.unsa.alerta360.data.network.NetworkMonitor
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class Alerta360Application : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()



        NetworkMonitor.startListening(this)
        // Inicializar FCM
        initializeFcm()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    private fun initializeFcm() {
        // La inicialización real se hará desde el ViewModel principal
        // cuando el usuario esté autenticado
    }
}