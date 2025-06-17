package com.unsa.alerta360.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.model.IncidentWithUser
import com.unsa.alerta360.presentation.login.lightCreamColor
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2

@Composable
fun HomeScreen(navController: NavController? = null) {
    val viewModel: HomeViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(color1, color2)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
            .padding(16.dp)
    ) {
        // Título
        Text(
            text = "Incidentes",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = lightCreamColor,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            modifier = Modifier.padding(vertical = 16.dp)
        )
        
        // Tabs con diseño de subrayado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            TabButtonWithUnderline(
                text = "Todos",
                isSelected = selectedTab is TabSelection.Todos,
                onClick = { viewModel.selectTab(TabSelection.Todos) }
            )
            Spacer(modifier = Modifier.width(32.dp))
            TabButtonWithUnderline(
                text = "Mi historial",
                isSelected = selectedTab is TabSelection.MiHistorial,
                onClick = { viewModel.selectTab(TabSelection.MiHistorial) }
            )
        }
        
        // Contenido basado en el estado
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = lightCreamColor)
                }
            }
            is HomeUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.refreshIncidents() },
                        colors = ButtonDefaults.buttonColors(containerColor = lightCreamColor)
                    ) {
                        Text("Reintentar", color = color2)
                    }
                }
            }
            is HomeUiState.Success -> {
                if (state.incidents.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay incidentes disponibles",
                            color = lightCreamColor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.incidents) { incidentWithUser ->
                            IncidentCard(
                                incidentWithUser = incidentWithUser,
                                onClick = {
                                    // Navegar a la pantalla de detalles del incidente
                                    navController?.navigate("incident_detail/${incidentWithUser.incident._id}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TabButtonWithUnderline(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            color = lightCreamColor,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Subrayado solo cuando está seleccionado
        if (isSelected) {
            Box(
                modifier = Modifier
                    .width(70.dp)
                    .height(2.dp)
                    .background(lightCreamColor)
            )
        }
    }
}

@Composable
fun IncidentCard(
    incidentWithUser: IncidentWithUser,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = lightCreamColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header con icono de usuario y nombre
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono de usuario (simulado)
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            color = color2,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        fontSize = 12.sp,
                        color = lightCreamColor
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = incidentWithUser.userName,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = color2
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Título del incidente
            Text(
                text = incidentWithUser.incident.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                ),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Descripción
            Text(
                text = incidentWithUser.incident.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Black.copy(alpha = 0.7f)
                ),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Botones de distrito y tipo de incidente
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Distrito
                Button(
                    onClick = { /* TODO: Implementar acción */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color2
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = incidentWithUser.incident.district,
                        style = MaterialTheme.typography.bodySmall,
                        color = lightCreamColor
                    )
                }
                
                // Botón Tipo de incidente
                OutlinedButton(
                    onClick = { /* TODO: Implementar acción */ },
                    border = BorderStroke(1.dp, color2),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = incidentWithUser.incident.incidentType,
                        style = MaterialTheme.typography.bodySmall,
                        color = color2
                    )
                }
            }
        }
    }
}
