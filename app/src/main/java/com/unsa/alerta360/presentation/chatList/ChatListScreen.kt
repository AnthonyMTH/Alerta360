package com.unsa.alerta360.presentation.chatList

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unsa.alerta360.domain.model.Chat
import com.unsa.alerta360.ui.theme.color1
import com.unsa.alerta360.ui.theme.color2

@Composable
fun ChatListScreen(onChatClick: (Chat) -> Unit) {
    val viewModel: ChatListViewModel = viewModel()
    val chats by viewModel.chats.collectAsState()

    val backgroundColor = Brush.verticalGradient(
        colors = listOf(color1, color2)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundColor)
    ) {
        LazyColumn {
            items(chats) { chat ->
                ChatItem(chat = chat, onChatClick = onChatClick)
            }
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onChatClick: (Chat) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onChatClick(chat) },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = chat.name, style = MaterialTheme.typography.headlineSmall)
            Text(text = chat.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewChatListScreen() {
    ChatListScreen(onChatClick = {})
}