package com.unsa.alerta360.presentation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2

@Composable
fun AccountScreen(
    accountViewModel: AccountViewModel = hiltViewModel()
) {
    val userData by accountViewModel.userData.collectAsState()
    val loading by accountViewModel.loading.collectAsState()
    val errorMessage by accountViewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        accountViewModel.loadAccount()
    }

    val backgroundBrush = Brush.verticalGradient(colors = listOf(color1, color2))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            if (errorMessage != null) {
                Text("Error: $errorMessage", color = Color.White)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Mi perfil",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    AccountField(
                        label = "Nombres",
                        value = userData.first_name ?: "",
                        onValueChange = { accountViewModel.onNombresChange(it) }
                    )

                    AccountField(
                        label = "Apellidos",
                        value = userData.last_name ?: "",
                        onValueChange = { accountViewModel.onApellidosChange(it) }
                    )

                    AccountField(
                        label = "DNI",
                        value = userData.dni ?: "",
                        onValueChange = { accountViewModel.onDireccionChange(it) }
                    )

                    AccountField(
                        label = "Distrito",
                        value = userData.district ?: "",
                        onValueChange = { accountViewModel.onDistritoChange(it) },
                        trailingIcon = {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                        }
                    )

                    AccountField(
                        label = "Celular",
                        value = userData.phone_number ?: "",
                        onValueChange = { accountViewModel.onCelularChange(it) },
                        trailingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White)
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = { /* cancelar */ },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = { accountViewModel.guardarPerfil() },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Text("Guardar", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        trailingIcon = trailingIcon,
        modifier = Modifier.fillMaxWidth(),
        textStyle = LocalTextStyle.current.copy(color = Color.White),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.White,
            unfocusedIndicatorColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.White,
            cursorColor = Color.White
        )
    )
}
