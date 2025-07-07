package com.unsa.alerta360.presentation.addIncident

import android.Manifest
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2
import com.unsa.alerta360.presentation.login.lightCreamColor

@Composable
fun AddIncidentScreen(navController: NavController) {

    val viewModel: AddIncidentViewModel = hiltViewModel()

    val backgroundColor = Brush.verticalGradient(
        colors = listOf(color1, color2)
    )

    val titulo by viewModel.titulo.collectAsState()
    val tipoIncidente by viewModel.tipoIncidente.collectAsState()
    val direccion by viewModel.direccion.collectAsState()

    val distrito by viewModel.distrito.collectAsState()
    val description by viewModel.description.collectAsState()
    val imageUri by viewModel.imageUri.collectAsState()

    val uiEvent by viewModel.uiEvent.collectAsState()

    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        ) {
            viewModel.guardar(context)
        } else {
            Toast.makeText(context, "Permiso de ubicación es requerido", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(uiEvent) {
        if (uiEvent is AddIncidentEvent.NavigateBack) {
            navController.popBackStack()
            viewModel.clearEvent()
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            Log.d("AddIncidentScreen", "Selected file Uri: $uri")
            viewModel.onFileSelected(uri)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor),
        contentAlignment = Alignment.TopCenter
    ) {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp)

        ) {
            Text(
                text = "Crear incidencia",
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = lightCreamColor,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = titulo,
                onValueChange = viewModel::onTituloChange,
                label = { Text("Título", fontWeight = FontWeight.Bold) },
                modifier = Modifier.fillMaxWidth(),
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

                )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                label = { Text("Descripción") },
                value = description,
                onValueChange = viewModel::onDescriptionChange,
                modifier = Modifier
                    .fillMaxWidth().height(100.dp),
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

                )
            Spacer(modifier = Modifier.height(16.dp))

            DropdownSelector(
                label = "Tipo de incidente",
                selected = tipoIncidente,
                options = viewModel.tiposIncidente,
                onSelected = viewModel::onTipoIncidenteChange
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = direccion,
                onValueChange = viewModel::onDireccionChange,
                label = { Text("Av. / Calle / Jirón") },
                modifier = Modifier.fillMaxWidth(),
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

                )

            Spacer(modifier = Modifier.height(8.dp))

            DropdownSelector(
                "Distrito",
                distrito,
                viewModel.distritos,
                viewModel::onDistritoChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .border(1.dp, Color.White, shape = RoundedCornerShape(4.dp))
                    .clickable { filePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
                    AsyncImage(
                        model = imageUri,
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("Subir foto", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.cancelar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text("Cancelar", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = lightCreamColor),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text("Guardar", color = color2)
            }

            when (val event = uiEvent) {
                is AddIncidentEvent.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                is AddIncidentEvent.Success -> {
                    Text(
                        text = event.message,
                        color = Color.Green,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Toast.makeText(
                        context,
                        (uiEvent as AddIncidentEvent.Success).message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is AddIncidentEvent.Error -> {
                    Text(
                        text = event.message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                else -> {
                    // No hacer nada si el estado es NavigateBack o cualquier otro no manejado
                }
            }

        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSelector(
    label: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = lightCreamColor,
                unfocusedTextColor = lightCreamColor,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                cursorColor = lightCreamColor,
                focusedIndicatorColor = lightCreamColor,
                unfocusedIndicatorColor = lightCreamColor.copy(alpha = 0.7f),
                focusedLabelColor = lightCreamColor,
                unfocusedLabelColor = lightCreamColor.copy(alpha = 0.7f)
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
