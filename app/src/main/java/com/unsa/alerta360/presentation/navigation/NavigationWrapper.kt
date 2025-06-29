package com.unsa.alerta360.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.unsa.alerta360.navigation.MainScreen
import com.unsa.alerta360.presentation.home.HomeScreen
import com.unsa.alerta360.presentation.initial.InitialScreen
import com.unsa.alerta360.presentation.login.LoginScreen
import com.unsa.alerta360.presentation.register.RegisterScreen
import com.unsa.alerta360.presentation.splash.SplashScreen
import com.unsa.alerta360.presentation.incident.IncidentScreen

object AppRoutes {
    const val SPLASH = "splash"
    const val INITIAL = "initial"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val INCIDENT_DETAIL = "incident_detail"
}

@Composable
fun NavigationWrapper(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = AppRoutes.SPLASH) {
        composable(AppRoutes.SPLASH) {
            // El SplashScreen decidirá a dónde ir basándose en el estado de autenticación
            SplashScreen (
                onNavigateToInitial = {
                    navHostController.navigate(AppRoutes.INITIAL) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navHostController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoutes.INITIAL) {
            InitialScreen(
                navigateToLogin = { navHostController.navigate(AppRoutes.LOGIN) },
                navigateToRegister = { navHostController.navigate(AppRoutes.REGISTER) }
            )
        }
        composable(AppRoutes.LOGIN) {
            LoginScreen(onLoginSuccess = {
                // viewModel se inyectará automáticamente por Hilt si LoginViewModel está anotado con @HiltViewModel
                // y LoginScreen espera un LoginViewModel (con valor por defecto hiltViewModel())
                navHostController.navigate(AppRoutes.HOME) {
                    popUpTo(AppRoutes.INITIAL) { inclusive = true }
                    launchSingleTop = true
                }
            },
                onNavigateToRegister = {
                    navHostController.navigate(AppRoutes.REGISTER) {
                        popUpTo(AppRoutes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                })
        }
        composable(AppRoutes.REGISTER) {
            // El RegisterViewModel se obtiene aquí usando hiltViewModel()
            // RegisterScreen ya no necesita el parámetro 'authViewModel' si usa hiltViewModel() por defecto
            RegisterScreen(
                onRegistrationSuccess = {
                    // Decide a dónde navegar después de un registro exitoso.
                    // Navegar a Home y limpiar el backstack para que no pueda volver a register/initial.
                    navHostController.navigate(AppRoutes.HOME) {
                        popUpTo(AppRoutes.INITIAL) { inclusive = true } // Limpia hasta la pantalla inicial
                        launchSingleTop = true // Evita múltiples instancias de home
                    }
                },
                onNavigateToLogin = {
                    navHostController.navigate(AppRoutes.LOGIN) {
                        // Opcional: si se quiere limpiar la pantalla de registro del backstack
                        popUpTo(AppRoutes.REGISTER) { inclusive = true }
                        launchSingleTop = true // Evita múltiples instancias de login
                    }
                })
        }
        composable(AppRoutes.HOME) {
            MainScreen(navController = navHostController,
                onLogout = {
                    // Navegar al login y limpiar todo el backstack
                    navHostController.navigate(AppRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                })
        }
        composable("${AppRoutes.INCIDENT_DETAIL}/{incidentId}") { backStackEntry ->
            val incidentId = backStackEntry.arguments?.getString("incidentId") ?: ""
            IncidentScreen(
                incidentId = incidentId,
                onNavigateBack = {
                    navHostController.popBackStack()
                }
            )
        }
    }
}