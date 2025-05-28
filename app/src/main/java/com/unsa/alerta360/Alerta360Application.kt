package com.unsa.alerta360

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Alerta360Application : Application() {
    // Puedes añadir lógica de inicialización a nivel de aplicación aquí si es necesario en el futuro,
    // por ejemplo, para librerías de logging, analytics, etc.
    // Por ahora, para Hilt, solo se necesita la clase y la anotación.

    override fun onCreate() {
        super.onCreate()
        // Ejemplo: Timber.plant(Timber.DebugTree()) si usaras Timber para logging
    }
}