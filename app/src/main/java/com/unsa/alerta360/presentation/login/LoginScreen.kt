package com.unsa.alerta360.presentation.login

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unsa.alerta360.presentation.common.UiState
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2

val lightCreamColor = Color(0xFFFDF1CE) // Color crema claro

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(), // ViewModel se obtiene por defecto
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val loginState by viewModel.loginState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = uiEvent) {
        uiEvent?.let { event ->
            when (event) {
                is LoginUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = SnackbarDuration.Long
                    )
                    viewModel.onEventConsumed() // Marcar como consumido
                }
            }
        }
    }

    LaunchedEffect(key1 = loginState) {
        if (loginState is UiState.Success) {
            onLoginSuccess()
            viewModel.resetLoginState() // Evitar re-navegación
        }
        // El error se maneja a través de uiEvent para el Snackbar
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        val backgroundColor = Brush.verticalGradient(colors = listOf(color1, color2))
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(brush = backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth(0.85f)
            ) {
                Text(
                    text = "Bienvenido\nde Vuelta",
                    color = lightCreamColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    label = { Text("Correo Electrónico", color = lightCreamColor.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Email, "Icono de Email", tint = lightCreamColor) },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = lightCreamColor, unfocusedTextColor = lightCreamColor,
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent, cursorColor = lightCreamColor,
                        focusedIndicatorColor = lightCreamColor, unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                        focusedLabelColor = lightCreamColor, unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = loginState is UiState.Error && uiEvent != null // Mostrar error si el estado es Error y hay un evento de Snackbar
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = { Text("Contraseña", color = lightCreamColor.copy(alpha = 0.7f)) },
                    leadingIcon = { Icon(Icons.Default.Lock, "Icono de Contraseña", tint = lightCreamColor) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = lightCreamColor, unfocusedTextColor = lightCreamColor,
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent, cursorColor = lightCreamColor,
                        focusedIndicatorColor = lightCreamColor, unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                        focusedLabelColor = lightCreamColor, unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = loginState is UiState.Error && uiEvent != null
                )
                OutlinedButton(
                    onClick = { viewModel.loginUser() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 16.dp),
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, lightCreamColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = lightCreamColor
                    ),
                    enabled = loginState !is UiState.Loading
                ) {
                    if (loginState is UiState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = lightCreamColor)
                    } else {
                        Text("Ingresar", fontSize = 16.sp)
                    }
                }
                TextButton(
                    onClick = { onNavigateToRegister() },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("¿No tienes cuenta?", color = lightCreamColor, fontSize = 14.sp, textAlign = TextAlign.Center)
                }
            }
        }
    }
}