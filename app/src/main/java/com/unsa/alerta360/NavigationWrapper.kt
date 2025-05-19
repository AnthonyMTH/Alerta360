package com.unsa.alerta360

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.unsa.alerta360.presentation.initial.InitialScreen
import com.unsa.alerta360.presentation.login.LoginScreen
import com.unsa.alerta360.presentation.register.RegisterScreen

@Composable
fun NavigationWrapper(navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = "initial") {
        composable("initial") {
            InitialScreen()
        }
        composable("login") {
            LoginScreen()
        }
        composable("register") {
            RegisterScreen()
        }
    }
}