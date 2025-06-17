package com.unsa.alerta360.presentation.incident

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
                    .fillMaxHeight(0.8f)
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
}
