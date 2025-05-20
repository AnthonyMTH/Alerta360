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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.unsa.alerta360.R
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2

val lightCreamColor = Color(0xFFFDF1CE) // Color crema claro

@Composable
fun LoginScreen(
    auth: FirebaseAuth, onLoginSuccess: () -> Unit = {}, // Callback para navegar tras éxito
    onNavigateToRegister: () -> Unit = {}
) {
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(color1, color2)
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estado para el mensaje de error y SnackbarHostState
    var loginErrorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // LaunchedEffect para mostrar el Snackbar cuando cambie el mensaje de error
    LaunchedEffect(loginErrorMessage) {
        loginErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Long
            )
            loginErrorMessage = null // Resetea el mensaje después de mostrarlo
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent // Para que el fondo del Box se vea
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier
                    .fillMaxWidth(0.85f)
            ) {
                /*// Icono saludo
            Icon(
                painter = painterResource(id = R.drawable.ic_saludo),
                contentDescription = "Bienvenido",
                tint = lightCreamColor,
                modifier = Modifier.size(70.dp)
            )*/

                Text(
                    text = "Bienvenido\nde Vuelta",
                    color = lightCreamColor,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )

                // Campo Correo
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = {
                        Text(
                            "Correo Electrónico",
                            color = lightCreamColor.copy(alpha = 0.7f)
                        )
                    }, // Label un poco más tenue
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Icono de Email",
                            tint = lightCreamColor
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = lightCreamColor,
                        unfocusedTextColor = lightCreamColor,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = lightCreamColor,
                        focusedIndicatorColor = lightCreamColor, // Color del borde cuando está enfocado
                        unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f), // Color del borde cuando no está enfocado
                        focusedLabelColor = lightCreamColor, // Color del label cuando está enfocado
                        unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f) // Color del label
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Campo Contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña", color = lightCreamColor.copy(alpha = 0.7f)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Icono de Contraseña",
                            tint = lightCreamColor
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = lightCreamColor,
                        unfocusedTextColor = lightCreamColor,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        cursorColor = lightCreamColor,
                        focusedIndicatorColor = lightCreamColor,
                        unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                        focusedLabelColor = lightCreamColor,
                        unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Botón Ingresar
                OutlinedButton(
                    onClick = {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Log.i("login", "LOGIN OK")
                                    onLoginSuccess()
                                } else {
                                    val exception = task.exception
                                    loginErrorMessage = when (exception) {
                                        is FirebaseAuthInvalidUserException -> "El correo electrónico no está registrado."
                                        is FirebaseAuthInvalidCredentialsException -> "La contraseña es incorrecta."
                                        else -> "Error al iniciar sesión. Verifica tus credenciales e inténtalo de nuevo."
                                    }
                                    Log.i("login", "LOGIN FAILED")
                                }
                            }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(top = 16.dp), // Espacio extra arriba del botón
                    shape = RoundedCornerShape(25.dp),
                    border = BorderStroke(1.dp, lightCreamColor),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = lightCreamColor
                    )
                ) {
                    Text("Ingresar", fontSize = 16.sp)
                }

                // Texto inferior
                TextButton(
                    onClick = {
                        onNavigateToRegister()
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "¿No tienes cuenta?",
                        color = lightCreamColor,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}