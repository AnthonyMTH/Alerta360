package com.unsa.alerta360.presentation.register

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unsa.alerta360.viewmodel.auth.AuthViewModel
import com.unsa.alerta360.viewmodel.auth.RegistrationResult

val DarkBlueBackground = Color(0xFF2A5F7B) // Un azul oscuro/petróleo
val LightTextAndIcons = Color(0xFFD0E0E8) // Un gris azulado claro para texto e iconos
val TextFieldBorderFocused = Color(0xFF65C2F5) // Azul claro para el borde enfocado
val ButtonBeigeBackground = Color(0xFFF5EFE3) // Beige para el botón
val ButtonDarkText = Color(0xFF3A3A3A)       // Texto oscuro para el botón
val LinkTextColor = Color(0xFFB0D7E8)       // Un azul más claro para el enlace

val arequipaDistricts = listOf(
    "Alto Selva Alegre", "Arequipa", "Cayma", "Cerro Colorado",
    "Characato", "Chiguata", "Jacobo Hunter", "José Luis Bustamante y Rivero",
    "La Joya", "Mariano Melgar", "Miraflores", "Mollebaya",
    "Paucarpata", "Pocsi", "Polobaya", "Quequeña",
    "Sabandía", "Sachaca", "San Juan de Siguas", "San Juan de Tarucani",
    "Santa Isabel de Siguas", "Santa Rita de Siguas", "Socabaya", "Tiabaya",
    "Uchumayo", "Vitor", "Yanahuara", "Yarabamba", "Yura"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    authViewModel: AuthViewModel = viewModel(), // Inyecta el ViewModel
    onRegistrationSuccess: () -> Unit = {}, // Callback para navegar tras éxito
    onNavigateToLogin: () -> Unit = {} // Callback para navegar a login)
)
{
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var distrito by remember { mutableStateOf("") }

    // Para el Dropdown de distrito
    var distritoExpanded by remember { mutableStateOf(false) }
    var selectedDistrito by remember { mutableStateOf(arequipaDistricts.firstOrNull() ?: "") }

    val registrationResult by authViewModel.registrationState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(registrationResult) {
        when (val result = registrationResult) {
            is RegistrationResult.Success -> {
                snackbarHostState.showSnackbar(
                    message = "¡Registro exitoso!",
                    duration = SnackbarDuration.Short
                )
                // kotlinx.coroutines.delay(1000)
                onRegistrationSuccess() // Llama al callback para navegar
                authViewModel.resetRegistrationState() // Resetea el estado
            }

            is RegistrationResult.Error -> {
                snackbarHostState.showSnackbar(
                    message = "Error: ${result.message}",
                    duration = SnackbarDuration.Long
                )
                authViewModel.resetRegistrationState() // Resetea el estado
            }

            RegistrationResult.Loading -> {
                // Se podría mostrar un ProgressIndicator global
            }

            RegistrationResult.Idle -> {
                // Estado inicial o reseteado
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(color1, color2)
                    )
                )
                .padding(paddingValues) // Aplicar padding del Scaffold
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // --- Título e Icono ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Crea una\nCuenta",
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightTextAndIcons,
                    lineHeight = 40.sp,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = "User Icon",
                    tint = LightTextAndIcons,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- Campos de Texto ---
            CustomOutlinedTextField(
                value = nombres,
                onValueChange = { nombres = it },
                label = "Nombres"
            )
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = apellidos,
                onValueChange = { apellidos = it },
                label = "Apellidos"
            )
            Spacer(modifier = Modifier.height(12.dp))

            // --- DNI ---
            OutlinedTextField(
                value = dni,
                onValueChange = { dni = it },
                label = { Text("DNI", color = LightTextAndIcons) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = LightTextAndIcons,
                    unfocusedTextColor = LightTextAndIcons,
                    cursorColor = LightTextAndIcons,
                    focusedBorderColor = TextFieldBorderFocused,
                    unfocusedBorderColor = LightTextAndIcons,
                    focusedLabelColor = LightTextAndIcons,
                    unfocusedLabelColor = LightTextAndIcons
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = celular,
                onValueChange = { celular = it },
                label = "Celular",
                trailingIcon = {
                    Icon(Icons.Outlined.Phone, "Phone Icon", tint = LightTextAndIcons)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = "Correo Electrónico",
                trailingIcon = {
                    Icon(Icons.Outlined.Email, "Email Icon", tint = LightTextAndIcons)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = contrasena,
                onValueChange = { contrasena = it },
                label = "Contraseña",
                trailingIcon = {
                    Icon(Icons.Outlined.Lock, "Lock Icon", tint = LightTextAndIcons)
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(12.dp))

            CustomOutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = "Dirección"
            )
            Spacer(modifier = Modifier.height(12.dp))

            // --- Dropdown distrito ---
            ExposedDropdownMenuBox(
                expanded = distritoExpanded,
                onExpandedChange = {
                    if (registrationResult != RegistrationResult.Loading) {
                        distritoExpanded = !distritoExpanded
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedDistrito,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Distrito", color = LightTextAndIcons) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = distritoExpanded)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = LightTextAndIcons,
                        unfocusedTextColor = LightTextAndIcons,
                        cursorColor = LightTextAndIcons,
                        focusedBorderColor = LightTextAndIcons,
                        unfocusedBorderColor = LightTextAndIcons,
                        focusedLabelColor = LightTextAndIcons,
                        unfocusedLabelColor = LightTextAndIcons,
                        focusedTrailingIconColor = LightTextAndIcons,
                        unfocusedTrailingIconColor = LightTextAndIcons,
                        disabledTextColor = LightTextAndIcons.copy(alpha = 0.7f),
                        disabledBorderColor = LightTextAndIcons.copy(alpha = 0.5f),
                        disabledLabelColor = LightTextAndIcons.copy(alpha = 0.7f),
                        disabledTrailingIconColor = LightTextAndIcons.copy(alpha = 0.7f)
                    ),
                    modifier = Modifier
                        .menuAnchor() // Anclar el menú
                        .fillMaxWidth(),
                    enabled = registrationResult != RegistrationResult.Loading
                )
                ExposedDropdownMenu(
                    expanded = distritoExpanded,
                    onDismissRequest = { distritoExpanded = false },
                    modifier = Modifier.background(DarkBlueBackground) // Fondo del menú
                ) {
                    arequipaDistricts.forEach { districtName ->
                        DropdownMenuItem(
                            text = { Text(districtName, color = LightTextAndIcons) },
                            onClick = {
                                selectedDistrito = districtName
                                distritoExpanded = false
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = LightTextAndIcons
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // --- Botón Crear Cuenta ---
            Button(
                onClick = {
                    // Validaciones básicas
                    if (nombres.isBlank() || apellidos.isBlank() || dni.isBlank() ||
                        celular.isBlank() || correo.isBlank() || contrasena.isBlank() ||
                        direccion.isBlank() || selectedDistrito.isBlank()) {
                        Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                        Toast.makeText(context, "Correo electrónico no válido.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    if (contrasena.length < 6) {
                        Toast.makeText(context, "La contraseña debe tener al menos 6 caracteres.", Toast.LENGTH_LONG).show()
                        return@Button
                    }

                    authViewModel.registerUser(
                        email = correo.trim(),
                        password = contrasena,
                        nombres = nombres.trim(),
                        apellidos = apellidos.trim(),
                        dni = dni.trim(),
                        celular = celular.trim(),
                        direccion = direccion.trim(),
                        distrito = selectedDistrito
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBeigeBackground,
                    contentColor = ButtonDarkText
                ),
                enabled = registrationResult != RegistrationResult.Loading // Deshabilita el botón mientras carga

            ) {
                if (registrationResult == RegistrationResult.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = ButtonDarkText,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Crear Cuenta", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Enlace "¿Ya tienes cuenta?" ---
            Text(
                text = "¿Ya tienes cuenta?",
                color = LinkTextColor,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(enabled = registrationResult != RegistrationResult.Loading) {
                    onNavigateToLogin() }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: androidx.compose.ui.text.input.VisualTransformation = androidx.compose.ui.text.input.VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    enabled: Boolean = true // Añadido para deshabilitar
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = LightTextAndIcons) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = LightTextAndIcons,
            unfocusedTextColor = LightTextAndIcons,
            cursorColor = LightTextAndIcons,
            focusedBorderColor = LightTextAndIcons,
            unfocusedBorderColor = LightTextAndIcons,
            focusedLabelColor = LightTextAndIcons,
            unfocusedLabelColor = LightTextAndIcons,
            disabledTrailingIconColor = LightTextAndIcons.copy(alpha = 0.7f),
            focusedTrailingIconColor = LightTextAndIcons,
            unfocusedTrailingIconColor = LightTextAndIcons,
            disabledTextColor = LightTextAndIcons.copy(alpha = 0.7f),
            disabledBorderColor = LightTextAndIcons.copy(alpha = 0.5f),
            disabledLabelColor = LightTextAndIcons.copy(alpha = 0.7f)
        ),
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        enabled = enabled // Aplicar estado enabled
    )
}