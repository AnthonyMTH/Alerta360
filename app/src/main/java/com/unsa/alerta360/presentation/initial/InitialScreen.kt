package com.unsa.alerta360.presentation.initial

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.unsa.alerta360.R
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2

@Composable
fun InitialScreen( navigateToLogin: () -> Unit = {}, navigateToRegister: () -> Unit = {}) {
    val backgroundColor = Brush.verticalGradient(
        colors = listOf(color1, color2)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            /*// Icono de alerta
            Icon(
                painter = painterResource(id = R.drawable.ic_alerta), // Asegúrate de tener este vector en tu carpeta drawable
                contentDescription = "Alerta",
                tint = Color(0xFFFDF1CE),
                modifier = Modifier
                    .size(96.dp)
            )*/

            // Título
            Text(
                text = "Alerta360",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFDF1CE)
            )

            // Botón "Iniciar Sesión"
            OutlinedButton(
                onClick = { navigateToLogin() },
                border = BorderStroke(1.dp, Color(0xFFFDF1CE)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color(0xFFFDF1CE)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp)
            ) {
                Text("Iniciar Sesión")
            }

            // Botón "Crear Cuenta"
            Button(
                onClick = { navigateToRegister() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFDF1CE),
                    contentColor = Color(0xFF00334D)
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp)
            ) {
                Text("Crear Cuenta")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InitialScreenPreview() {
    InitialScreen()
}