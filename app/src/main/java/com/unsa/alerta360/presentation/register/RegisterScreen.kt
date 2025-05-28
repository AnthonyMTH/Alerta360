package com.unsa.alerta360.presentation.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unsa.alerta360.presentation.common.UiState
import com.unsa.alerta360.presentation.login.lightCreamColor
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2

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
    viewModel: RegisterViewModel = hiltViewModel(),
    onRegistrationSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val nombres by viewModel.nombres.collectAsState()
    val apellidos by viewModel.apellidos.collectAsState()
    val dni by viewModel.dni.collectAsState()
    val celular by viewModel.celular.collectAsState()
    val correo by viewModel.correo.collectAsState()
    val contrasena by viewModel.contrasena.collectAsState()
    val direccion by viewModel.direccion.collectAsState()
    val selectedDistrito by viewModel.distrito.collectAsState()
    val distritoExpanded by viewModel.distritoExpanded.collectAsState()

    val registrationState by viewModel.registrationState.collectAsState()
    val uiEvent by viewModel.uiEvent.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = uiEvent) {
        uiEvent?.let { event ->
            when (event) {
                is RegisterUiEvent.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = event.message,
                        duration = if (registrationState is UiState.Success) SnackbarDuration.Short else SnackbarDuration.Long
                    )
                    viewModel.onEventConsumed()
                }
            }
        }
    }

    LaunchedEffect(key1 = registrationState) {
        if (registrationState is UiState.Success) {
            // kotlinx.coroutines.delay(1000) // Considera si este delay es necesario
            onRegistrationSuccess()
            viewModel.resetRegistrationState()
        }
        // El error se maneja a través de uiEvent para el Snackbar
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Brush.verticalGradient(listOf(color1, color2)))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Crear Cuenta",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = lightCreamColor
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nombres,
                onValueChange = viewModel::onNombresChange,
                label = { Text("Nombres") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = lightCreamColor, unfocusedTextColor = lightCreamColor,
                    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent, cursorColor = lightCreamColor,
                    focusedIndicatorColor = lightCreamColor, unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                    focusedLabelColor = lightCreamColor, unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
                ),
            )
            OutlinedTextField(
                value = apellidos,
                onValueChange = viewModel::onApellidosChange,
                label = { Text("Apellidos") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = lightCreamColor, unfocusedTextColor = lightCreamColor,
                    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent, cursorColor = lightCreamColor,
                    focusedIndicatorColor = lightCreamColor, unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                    focusedLabelColor = lightCreamColor, unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
                ),
            )
            OutlinedTextField(
                value = dni,
                onValueChange = viewModel::onDniChange,
                label = { Text("DNI") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = lightCreamColor, unfocusedTextColor = lightCreamColor,
                    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent, cursorColor = lightCreamColor,
                    focusedIndicatorColor = lightCreamColor, unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                    focusedLabelColor = lightCreamColor, unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
                ),
            )
            OutlinedTextField(
                value = celular,
                onValueChange = viewModel::onCelularChange,
                label = { Text("Celular") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = lightCreamColor, unfocusedTextColor = lightCreamColor,
                    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent, cursorColor = lightCreamColor,
                    focusedIndicatorColor = lightCreamColor, unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                    focusedLabelColor = lightCreamColor, unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
                ),
            )
            OutlinedTextField(
                value = correo,
                onValueChange = viewModel::onCorreoChange,
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = lightCreamColor, unfocusedTextColor = lightCreamColor,
                    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent, cursorColor = lightCreamColor,
                    focusedIndicatorColor = lightCreamColor, unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                    focusedLabelColor = lightCreamColor, unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
                ),
            )
            OutlinedTextField(
                value = contrasena,
                onValueChange = viewModel::onContrasenaChange,
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = lightCreamColor, unfocusedTextColor = lightCreamColor,
                    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent, cursorColor = lightCreamColor,
                    focusedIndicatorColor = lightCreamColor, unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                    focusedLabelColor = lightCreamColor, unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
                ),
            )
            OutlinedTextField(
                value = direccion,
                onValueChange = viewModel::onDireccionChange,
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = lightCreamColor, unfocusedTextColor = lightCreamColor,
                    focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent, cursorColor = lightCreamColor,
                    focusedIndicatorColor = lightCreamColor, unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                    focusedLabelColor = lightCreamColor, unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
                ),
            )

            ExposedDropdownMenuBox(
                expanded = distritoExpanded,
                onExpandedChange = viewModel::onDistritoExpandedChange,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedDistrito.ifEmpty { "Selecciona un distrito" }, // Placeholder si está vacío
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Distrito") },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = lightCreamColor, unfocusedTextColor = lightCreamColor,
                        focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent, cursorColor = lightCreamColor,
                        focusedIndicatorColor = lightCreamColor, unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                        focusedLabelColor = lightCreamColor, unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
                    ),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = distritoExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = distritoExpanded,
                    onDismissRequest = { viewModel.onDistritoExpandedChange(false) }
                ) {
                    arequipaDistricts.forEach { district ->
                        DropdownMenuItem(
                            text = { Text(district) },
                            onClick = {
                                viewModel.onDistritoChange(district)
                                viewModel.onDistritoExpandedChange(false)
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.registerUser() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBeigeBackground,
                    contentColor = ButtonDarkText
                ),
                enabled = registrationState !is UiState.Loading
            ) {
                if (registrationState == UiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = ButtonDarkText,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Crear Cuenta", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            TextButton(onClick = onNavigateToLogin, modifier = Modifier.fillMaxWidth()) {
                Text("¿Ya tienes cuenta? Inicia Sesión", color = lightCreamColor)
            }
        }
    }
}
