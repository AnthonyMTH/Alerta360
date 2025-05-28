package com.unsa.alerta360.presentation.addIncident

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unsa.alerta360.presentation.login.lightCreamColor


@Composable
fun AddIncidentScreen(viewModel: AddIncidentViewModel = viewModel()) {
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(color1, color2)
    )

    val titulo = viewModel.titulo
    val tipoIncidente = viewModel.tipoIncidente
    val direccion = viewModel.direccion
    val departamento = viewModel.departamento
    val provincia = viewModel.provincia
    val distrito = viewModel.distrito
    val description = viewModel.description
    val imageUri = viewModel.imageUri

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
                .padding(16.dp)

        ) {
            Text(
                text = "Crear incidencia",
                style = MaterialTheme.typography.headlineSmall.copy(color = Color.White, fontWeight = FontWeight.Bold)
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

            DropdownSelector("Departamento", departamento, viewModel.departamentos, viewModel::onDepartamentoChange)
            DropdownSelector("Provincia", provincia, viewModel.provincias, viewModel::onProvinciaChange)
            DropdownSelector("Distrito", distrito, viewModel.distritos, viewModel::onDistritoChange)

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .border(1.dp, Color.White, shape = RoundedCornerShape(4.dp))
                    .clickable { filePickerLauncher.launch("*/*") },
                contentAlignment = Alignment.Center
            ) {
                Text("Subir foto", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button (
                onClick = { viewModel.cancelar() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, Color.White)
            ) {
                Text("Cancelar", color = Color.White)
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
    Log.d("EXPANDED", expanded.toString())

    ExposedDropdownMenuBox(
         expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true).
            fillMaxWidth(),


            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)

            },
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
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            //modifier = Modifier.fillMaxWidth()
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
