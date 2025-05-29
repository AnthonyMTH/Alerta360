package com.unsa.alerta360.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unsa.alerta360.presentation.account.AccountScreen
import com.unsa.alerta360.presentation.addIncident.AddIncidentScreen
import com.unsa.alerta360.presentation.home.HomeScreen
import com.unsa.alerta360.presentation.map.MapScreen
import com.unsa.alerta360.presentation.messages.MessagesScreen

@Composable
fun MainScreen() {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBarComponent(mainNavController)
        }
    ) { innerPadding ->
        NavHost(
            navController = mainNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") { HomeScreen() }
            composable("map") { MapScreen() }
            composable("addIncident") { AddIncidentScreen(navController = mainNavController) }
            composable("messages") { MessagesScreen() }
            composable("account") { AccountScreen() }
        }
    }
}