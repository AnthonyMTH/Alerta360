package com.unsa.alerta360



import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.unsa.alerta360.presentation.navigation.NavigationWrapper
import com.unsa.alerta360.presentation.viewmodel.MainViewModel
import com.unsa.alerta360.ui.theme.Alerta360Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private lateinit var navHostController: NavHostController
    private val mainViewModel: MainViewModel by viewModels()

    companion object {
        private const val TAG = "MainActivity"
    }

    // Registro para solicitar permisos de notificación
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        mainViewModel.setNotificationPermissionGranted(isGranted)
        if (isGranted) {
            Log.d(TAG, "Notification permission granted")
        } else {
            Log.d(TAG, "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(TAG, "🚀 MainActivity onCreate started")

        // FORZAR inicialización de FCM desde el inicio
        initializeFcmService()

        // Verificar si llegamos desde una notificación
        handleNotificationIntent()

        // Solicitar permisos de notificación
        requestNotificationPermission()

        setContent {
            navHostController = rememberNavController()
            Alerta360Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val notificationPermissionGranted by mainViewModel.notificationPermissionGranted.collectAsState()
                    val fcmInitialized by mainViewModel.fcmInitialized.collectAsState()
                    val currentUserId by mainViewModel.currentUserId.collectAsState()

                    // Configurar listener para token FCM cuando tengamos userId
                    LaunchedEffect(currentUserId) {
                        currentUserId?.let { userId ->
                            Log.d(TAG, "👤 User ID set: $userId")
                            setupFcmTokenListener(userId)
                            if (notificationPermissionGranted && !fcmInitialized) {
                                mainViewModel.initializeFcm(userId)
                            }
                        }
                    }
                    NavigationWrapper(navHostController = navHostController)
                    // Tu UI principal aquí
                    // MainScreen() o el composable principal de tu app
                }
            }
        }
    }
    private fun initializeFcmService() {
        Log.d(TAG, "🔥 Forcing FCM Service initialization...")

        try {
            // Obtener token inmediatamente para forzar inicialización
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG, "🎯 FCM Token obtained on startup: ${token.take(20)}...")

                    // Suscribirse inmediatamente a tópicos de prueba
                    subscribeToTestTopics()
                } else {
                    Log.e(TAG, "❌ Failed to get FCM token on startup", task.exception)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 Exception initializing FCM service", e)
        }
    }

    private fun subscribeToTestTopics() {
        Log.d(TAG, "🔔 Subscribing to test topics immediately...")

        val messaging = FirebaseMessaging.getInstance()

        // Lista de tópicos que vimos en el backend
        val topics = listOf(
            "all_incidents",
            "emergency_alerts",
            "location_socabaya",
            "debug_test"
        )

        topics.forEach { topic ->
            messaging.subscribeToTopic(topic)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "✅ Subscribed to: $topic")
                    } else {
                        Log.e(TAG, "❌ Failed to subscribe to: $topic", task.exception)
                    }
                }
        }
    }
    private fun handleNotificationIntent() {
        // Verificar si la actividad se abrió desde una notificación
        intent.getStringExtra("incident_id")?.let { incidentId ->
            Log.d(TAG, "App opened from notification with incident ID: $incidentId")
            mainViewModel.handleIncidentFromNotification(incidentId)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    mainViewModel.setNotificationPermissionGranted(true)
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            // Para versiones anteriores a Android 13, las notificaciones están permitidas por defecto
            mainViewModel.setNotificationPermissionGranted(true)
        }
    }

    private fun setupFcmTokenListener(userId: String) {
        Log.d(TAG, "🔧 Setting up FCM token listener for user: $userId")

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "❌ Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }

            // Obtener nuevo token FCM
            val token = task.result
            Log.d(TAG, "📱 FCM Registration Token obtained: ${token.take(20)}...")

            // Enviar token al servidor con el userId
            mainViewModel.updateFcmToken(userId, token)

            // Suscribirse manualmente a tópicos para debug
            subscribeToTopicsForDebugging()
        }
    }

    private fun subscribeToTopicsForDebugging() {
        Log.d(TAG, "🔔 Manually subscribing to topics for debugging...")

        val messaging = FirebaseMessaging.getInstance()

        // Suscribirse a all_incidents
        messaging.subscribeToTopic("all_incidents")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "✅ Successfully subscribed to: all_incidents")
                } else {
                    Log.e(TAG, "❌ Failed to subscribe to: all_incidents", task.exception)
                }
            }

        // Suscribirse a emergency_alerts
        messaging.subscribeToTopic("emergency_alerts")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "✅ Successfully subscribed to: emergency_alerts")
                } else {
                    Log.e(TAG, "❌ Failed to subscribe to: emergency_alerts", task.exception)
                }
            }

        // También suscribirse a algunos tópicos específicos que viste en el backend
        messaging.subscribeToTopic("location_socabaya")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "✅ Successfully subscribed to: location_socabaya")
                } else {
                    Log.e(TAG, "❌ Failed to subscribe to: location_socabaya", task.exception)
                }
            }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Manejar nuevos intents cuando la app ya está abierta
        intent?.getStringExtra("incident_id")?.let { incidentId ->
            Log.d(TAG, "New intent with incident ID: $incidentId")
            mainViewModel.handleIncidentFromNotification(incidentId)
        }
    }
}