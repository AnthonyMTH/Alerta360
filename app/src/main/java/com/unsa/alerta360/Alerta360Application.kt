package com.unsa.alerta360

import android.app.Application
import com.unsa.alerta360.data.sync.SyncScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class Alerta360Application : Application() {
    
    @Inject
    lateinit var syncScheduler: SyncScheduler
    
    // Scope a nivel de aplicación
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        
        // Configurar la sincronización automática
        setupSyncConfiguration()
    }
    
    private fun setupSyncConfiguration() {
        // Configurar sincronización periódica cada 6 horas
        syncScheduler.startPeriodicSync(intervalHours = 6)
    }
}