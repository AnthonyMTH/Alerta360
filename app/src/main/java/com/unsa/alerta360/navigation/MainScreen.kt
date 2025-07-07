package com.unsa.alerta360.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.unsa.alerta360.presentation.account.AccountScreen
import com.unsa.alerta360.presentation.addIncident.AddIncidentScreen
import com.unsa.alerta360.presentation.home.HomeScreen
import com.unsa.alerta360.presentation.incident.IncidentScreen
import com.unsa.alerta360.presentation.incident.IncidentViewModel
import com.unsa.alerta360.presentation.map.HeatmapScreen
import com.unsa.alerta360.presentation.messages.MessagesScreen

@Composable
fun MainScreen(navController: NavHostController? = null, onLogout: () -> Unit = {}) {
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
            composable("home") { HomeScreen(navController = navController) }
            //composable("home") {
            //    val incidentViewModel: IncidentViewModel = viewModel()
            //    IncidentScreen(viewModel = incidentViewModel)
            //}
            composable("map") { HeatmapScreen(openDrawer = {}, navController = navController!!) }
            composable("addIncident") { AddIncidentScreen(navController = mainNavController) }
            composable("messages") { MessagesScreen() }
            composable("account") {
                AccountScreen(
                    onLogoutSuccess = onLogout
                )
            }
        }
    }
}