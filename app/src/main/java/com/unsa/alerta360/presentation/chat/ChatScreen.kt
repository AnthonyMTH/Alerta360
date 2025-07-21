package com.unsa.alerta360.presentation.chat

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.unsa.alerta360.domain.model.Message
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2
import com.unsa.alerta360.ui.theme.lightCreamColor
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatScreen(chatId: String?, chatName: String?) {
    val viewModel: ChatViewModel = hiltViewModel()
    val messages by viewModel.messages.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var text by remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        if (chatId != null) {
            viewModel.loadMessages(chatId)
        }
    }

    DisposableEffect(chatId) {
        onDispose {
            viewModel.resetChatState()
        }
    }

    val backgroundColor = Brush.verticalGradient(
        colors = listOf(color1, color2)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
    ) {
        // Title
        Text(
            text = chatName ?: "Chat",
            style = MaterialTheme.typography.headlineMedium.copy(
                color = lightCreamColor,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            ),
            modifier = Modifier.padding(16.dp)
        )

        when (val state = uiState) {
            is ChatUiState.Loading -> {
                Log.d("ChatScreen", "UI State: Loading")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(color = lightCreamColor)
                        Text(
                            text = "Conectando al chat...",
                            color = lightCreamColor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            is ChatUiState.Error -> {
                Log.d("ChatScreen", "UI State: Error - ${state.message}")
                if (state.message.contains("StandaloneCoroutine was cancelled")) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(color = lightCreamColor)
                            Text(
                                text = "Cargando mensajes...",
                                color = lightCreamColor,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = state.message,
                                color = Color.Red,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                            Button(
                                onClick = { chatId?.let { viewModel.loadMessages(it) } },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = lightCreamColor.copy(alpha = 0.2f),
                                    contentColor = lightCreamColor
                                )
                            ) {
                                Text("Reintentar conexión")
                            }
                        }
                    }
                }
            }
            is ChatUiState.Success -> {
                Log.d("ChatScreen", "UI State: Success - Messages count: ${messages.size}")
                if (messages.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay mensajes aún",
                            color = lightCreamColor,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        reverseLayout = true
                    ) {
                        items(messages.reversed()) { message ->
                            MessageItem(message = message)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                label = { Text("Message") },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = com.unsa.alerta360.presentation.login.lightCreamColor,
                    unfocusedTextColor = com.unsa.alerta360.presentation.login.lightCreamColor,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    cursorColor = com.unsa.alerta360.presentation.login.lightCreamColor,
                    focusedIndicatorColor = com.unsa.alerta360.presentation.login.lightCreamColor, // Color del borde cuando está enfocado
                    unfocusedIndicatorColor = com.unsa.alerta360.presentation.login.lightCreamColor.copy(alpha = 0.7f), // Color del borde cuando no está enfocado
                    focusedLabelColor = com.unsa.alerta360.presentation.login.lightCreamColor, // Color del label cuando está enfocado
                    unfocusedLabelColor = com.unsa.alerta360.presentation.login.lightCreamColor.copy(alpha = 0.7f) // Color del label
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                chatId?.let { viewModel.sendMessage(it, text) }
                text = ""
            }) {
                Text("Send")
            }
        }
    }
}

@Composable
fun MessageItem(message: Message) {
    val formatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("America/Lima") } }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium,
        color = lightCreamColor.copy(alpha = 0.2f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = message.senderName ?: "Desconocido",
                    style = MaterialTheme.typography.titleMedium,
                    color = lightCreamColor
                )
                Text(
                    text = formatter.format(Date(message.timestamp ?: 0L)),
                    style = MaterialTheme.typography.bodySmall,
                    color = lightCreamColor
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            message.text?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    color = lightCreamColor
                )
            }
        }
    }
}
