package com.unsa.alerta360.presentation.register

import android.os.Bundle
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

// --- Colores inspirados en la imagen ---
val DarkBlueBackground = Color(0xFF2A5F7B) // Un azul oscuro/petróleo
val LightTextAndIcons = Color(0xFFD0E0E8) // Un gris azulado claro para texto e iconos
val TextFieldBorderFocused = Color(0xFF65C2F5) // Azul claro para el borde enfocado (como en DNI)
val ButtonBeigeBackground = Color(0xFFF5EFE3) // Beige para el botón
val ButtonDarkText = Color(0xFF3A3A3A)       // Texto oscuro para el botón
val LinkTextColor = Color(0xFFB0D7E8)       // Un azul más claro para el enlace

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(auth: FirebaseAuth) {
    var nombres by remember { mutableStateOf("") }
    var apellidos by remember { mutableStateOf("") }
    var dni by remember { mutableStateOf("") }
    var celular by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var departamento by remember { mutableStateOf("") }
    var provincia by remember { mutableStateOf("") }
    var distrito by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                    colors = listOf(color1, color2)
                ))
                .padding(horizontal = 32.dp, vertical = 24.dp)
                .verticalScroll(rememberScrollState()), // Para que sea desplazable si no cabe
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // --- Título y Icono ---
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

            // DNI (con el borde azul como si estuviera enfocado para la demo)
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
                    focusedBorderColor = TextFieldBorderFocused, // Borde azul destacado
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

            // --- Campos tipo Dropdown (simulados con TextField y icono) ---
            CustomDropdownTextField(
                value = departamento,
                onValueChange = { departamento = it }, // En una app real, esto abriría un menú
                label = "Departamento"
            )
            Spacer(modifier = Modifier.height(12.dp))

            CustomDropdownTextField(
                value = provincia,
                onValueChange = { provincia = it },
                label = "Provincia"
            )
            Spacer(modifier = Modifier.height(12.dp))

            CustomDropdownTextField(
                value = distrito,
                onValueChange = { distrito = it },
                label = "Distrito"
            )

            Spacer(modifier = Modifier.height(30.dp))

            // --- Botón Crear Cuenta ---
            Button(
                onClick = { /* TODO: Lógica de crear cuenta */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp), // Bordes redondeados
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonBeigeBackground,
                    contentColor = ButtonDarkText
                )
            ) {
                Text("Crear Cuenta", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Enlace "¿Ya tienes cuenta?" ---
            Text(
                text = "¿Ya tienes cuenta?",
                color = LinkTextColor,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { /* TODO: Navegar a login */ }
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
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
            focusedBorderColor = LightTextAndIcons, // Podrías usar TextFieldBorderFocused aquí también
            unfocusedBorderColor = LightTextAndIcons,
            focusedLabelColor = LightTextAndIcons,
            unfocusedLabelColor = LightTextAndIcons,
            disabledTrailingIconColor = LightTextAndIcons,
            focusedTrailingIconColor = LightTextAndIcons,
            unfocusedTrailingIconColor = LightTextAndIcons
        ),
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier
) {
    // Para simular un dropdown, lo hacemos readOnly y clickable
    // En una app real, usarías ExposedDropdownMenuBox
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = LightTextAndIcons) },
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                // Aquí se abriría el menú desplegable
                // Para este ejemplo de diseño, onValueChange podría no hacer nada o
                // podrías poner un texto de ejemplo
                // onValueChange("Opción Seleccionada")
            },
        readOnly = true, // Importante para que no se pueda escribir
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = LightTextAndIcons,
            unfocusedTextColor = LightTextAndIcons,
            cursorColor = LightTextAndIcons,
            focusedBorderColor = LightTextAndIcons,
            unfocusedBorderColor = LightTextAndIcons,
            focusedLabelColor = LightTextAndIcons,
            unfocusedLabelColor = LightTextAndIcons,
            disabledTrailingIconColor = LightTextAndIcons,
            focusedTrailingIconColor = LightTextAndIcons,
            unfocusedTrailingIconColor = LightTextAndIcons,
            // Para que el texto se vea bien aunque esté "disabled" (por readOnly)
            disabledTextColor = LightTextAndIcons,
            disabledLabelColor = LightTextAndIcons,
            disabledBorderColor = LightTextAndIcons
        ),
        trailingIcon = {
            Icon(
                Icons.Filled.ArrowDropDown,
                "Dropdown Arrow",
                tint = LightTextAndIcons
            )
        }
    )
}


// --- Theme (puedes usar el que viene por defecto o el tuyo) ---
@Composable
fun MyApplicationTheme(content: @Composable () -> Unit) {
    // Por simplicidad, no defino un tema completo aquí.
    // MaterialTheme se encargará de algunos valores por defecto.
    MaterialTheme(
        typography = Typography( // Definir una tipografía base si es necesario
            bodyLarge = TextStyle(
                color = LightTextAndIcons // Color de texto por defecto
            )
        )
    ) {
        content()
    }
}