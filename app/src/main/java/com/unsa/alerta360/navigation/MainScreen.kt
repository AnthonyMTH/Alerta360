package com.unsa.alerta360.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.unsa.alerta360.presentation.account.AccountScreen
import com.unsa.alerta360.presentation.addIncident.AddIncidentScreen
import com.unsa.alerta360.presentation.chat.ChatScreen
import com.unsa.alerta360.presentation.chatList.ChatListScreen
import com.unsa.alerta360.presentation.home.HomeScreen
import com.unsa.alerta360.presentation.map.HeatmapScreen

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
            composable("map") { HeatmapScreen(navController = navController!!) }
            composable("addIncident") { AddIncidentScreen(navController = mainNavController) }
            composable("messages") { ChatListScreen(navController = mainNavController) }
            composable(
                route = "chat/{chatId}/{chatName}",
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                    navArgument("chatName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                ChatScreen(
                    chatId = backStackEntry.arguments?.getString("chatId"),
                    chatName = backStackEntry.arguments?.getString("chatName")?.let {
                        java.net.URLDecoder.decode(it, "UTF-8")
                    }
                )
            }
            composable("account") {
                AccountScreen(
                    onLogoutSuccess = onLogout
                )
            }
        }
    }
}