package com.unsa.alerta360

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.unsa.alerta360.presentation.home.HomeScreen
import com.unsa.alerta360.presentation.initial.InitialScreen
import com.unsa.alerta360.presentation.login.LoginScreen
import com.unsa.alerta360.presentation.register.RegisterScreen

@Composable
fun NavigationWrapper(navHostController: NavHostController, auth: FirebaseAuth) {
    NavHost(navController = navHostController, startDestination = "initial") {
        composable("initial") {
            InitialScreen(
                navigateToLogin = { navHostController.navigate("login") },
                navigateToRegister = { navHostController.navigate("register") }
            )
        }
        composable("login") {
            LoginScreen(auth, onLoginSuccess = {
                navHostController.navigate("home") {
                    popUpTo("initial") { inclusive = true }
                    launchSingleTop = true
                }
            },
                onNavigateToRegister = {
                    navHostController.navigate("register") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                })
        }
        composable("register") {
            RegisterScreen(
                onRegistrationSuccess = {
                    // Decide a dónde navegar después de un registro exitoso.
                    // Navegar a Home y limpiar el backstack para que no pueda volver a register/initial.
                    navHostController.navigate("home") {
                        popUpTo("initial") { inclusive = true } // Limpia hasta la pantalla inicial
                        launchSingleTop = true // Evita múltiples instancias de home
                    }
                },
                onNavigateToLogin = {
                    navHostController.navigate("login") {
                        // Opcional: si se quiere limpiar la pantalla de registro del backstack
                        popUpTo("register") { inclusive = true }
                        launchSingleTop = true // Evita múltiples instancias de login
                    }
                })
        }
        composable("home") {
            HomeScreen()
        }
    }
}