package com.unsa.alerta360.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.Brush
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2


@Composable
fun AccountScreen(viewModel: AccountViewModel = hiltViewModel()) {
    val nombres by viewModel.nombres.collectAsState()
    val apellidos by viewModel.apellidos.collectAsState()
    val direccion by viewModel.direccion.collectAsState()
    val celular by viewModel.celular.collectAsState()
    val distrito by viewModel.distrito.collectAsState()
    val provincia by viewModel.provincia.collectAsState()
    val departamento by viewModel.departamento.collectAsState()

    val distritos = listOf("Distrito A", "Distrito B", "Distrito C")
    val provincias = listOf("Provincia A", "Provincia B", "Provincia C")
    val departamentos = listOf("Departamento A", "Departamento B", "Departamento C")

    val backgroundColor = Brush.verticalGradient(colors = listOf(color1, color2))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column {
            Text(
                text = "Mi perfil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(nombres, viewModel::onNombresChange, label = { Text("Nombres", color = Color.White) }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(apellidos, viewModel::onApellidosChange, label = { Text("Apellidos", color = Color.White) }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(direccion, viewModel::onDireccionChange, label = { Text("Dirección", color = Color.White) }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            DropdownField("Distrito", distritos, distrito, viewModel::onDistritoChange)
            Spacer(modifier = Modifier.height(8.dp))
            DropdownField("Provincia", provincias, provincia, viewModel::onProvinciaChange)
            Spacer(modifier = Modifier.height(8.dp))
            DropdownField("Departamento", departamentos, departamento, viewModel::onDepartamentoChange)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = celular,
                onValueChange = viewModel::onCelularChange,
                label = { Text("Celular", color = Color.White) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { /* cancelar */ }) {
                    Text("Cancelar", color = Color.White)
                }
                Button(onClick = { viewModel.guardarPerfil() }) {
                    Text("Guardar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = Color.White) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item) },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}


