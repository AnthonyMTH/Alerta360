package com.unsa.alerta360.presentation.incident

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2
import com.unsa.alerta360.presentation.login.lightCreamColor
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun IncidentScreen(
    incidentId: String,
    onNavigateBack: () -> Unit,
    viewModel: IncidentViewModel = hiltViewModel()
) {
    LaunchedEffect(incidentId) {
        viewModel.loadIncidentById(incidentId)
    }

    val incident by viewModel.currentIncident
    val accountData by viewModel.accountData
    val isDeleting by viewModel.isDeleting
    val deleteSuccess by viewModel.deleteSuccess
    val deleteError by viewModel.deleteError

    // Estado para mostrar el diálogo de confirmación
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Efecto para navegar de vuelta cuando se elimine exitosamente
    LaunchedEffect(deleteSuccess) {
        if (deleteSuccess) {
            viewModel.resetDeleteSuccess()
            onNavigateBack()
        }
    }

    // Efecto para mostrar errores de eliminación
    LaunchedEffect(deleteError) {
        deleteError?.let { error ->
            // Aquí se podría mostrar un Toast o SnackBar
            viewModel.resetDeleteError()
        }
    }

    // Limpieza cuando se sale del screen
    DisposableEffect(Unit) {
        onDispose {
            viewModel.resetDeleteSuccess()
            viewModel.resetDeleteError()
        }
    }

    val backgroundColor = Brush.verticalGradient(
        colors = listOf(color1, color2)
    )

    incident?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón "atrás"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.background(
                        Color.White.copy(alpha = 0.2f),
                        RoundedCornerShape(8.dp)
                    )
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
            }

            Text(
                text = it.title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = lightCreamColor,
                modifier = Modifier.padding(8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Cambiado para dar espacio al botón
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = lightCreamColor
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    // Datos del usuario (cuenta)
                    accountData?.let { account ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "${account.first_name} ${account.last_name}",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(account.email ?: "Correo no disponible")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(it.ubication)
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(it.description, maxLines = 2, overflow = TextOverflow.Ellipsis)

                    Spacer(modifier = Modifier.height(8.dp))

                    it.evidence.firstOrNull()?.let { imageUrl ->
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Evidencia del incidente",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row {
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                            Text(it.district)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = Color.Black)) {
                            Text(it.incidentType)
                        }
                    }
                }
            }

            // Botón de eliminar (solo si es el dueño del incidente)
            if (viewModel.isOwner()) {
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showDeleteDialog = true },
                    enabled = !isDeleting,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isDeleting) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Eliminando...", color = Color.White, fontSize = 16.sp)
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Eliminar Incidente", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }

                    Spacer(modifier = Modifier.height(16.dp))
        }
    } ?: run {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.White)
        }
    }
    // Diálogo de confirmación para eliminar
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("Confirmar eliminación")
            },
            text = {
                Text("¿Estás seguro de que quieres eliminar este incidente? Esta acción no se puede deshacer.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteIncident()
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
