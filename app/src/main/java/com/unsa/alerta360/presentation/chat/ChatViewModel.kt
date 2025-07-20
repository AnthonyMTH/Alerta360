package com.unsa.alerta360.presentation.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.unsa.alerta360.data.network.socket.SocketEvent
import com.unsa.alerta360.data.network.socket.SocketManager
import com.unsa.alerta360.data.util.DateLongAdapter
import com.unsa.alerta360.domain.model.Message
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.usecase.auth.GetCurrentUserUseCase
import com.unsa.alerta360.domain.usecase.auth.GetDetailsUserUseCase
import com.unsa.alerta360.domain.usecase.chat.GetMessagesUseCase
import com.unsa.alerta360.domain.usecase.chat.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatUiState {
    object Loading : ChatUiState()
    object Success : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getDetailsUserUseCase: GetDetailsUserUseCase,
    private val socketManager: SocketManager // Inyectar SocketManager
) : ViewModel() {

    private val gson: Gson = GsonBuilder().create()

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    val userId = getCurrentUserUseCase()!!.uid
    private var currentChatId: String? = null

    init {
        viewModelScope.launch {
            val userDetails = getDetailsUserUseCase(userId)
            val userName = userDetails.first_name ?: "Usuario" // Default if null

            // TODO: Replace with your actual backend WebSocket URL
            val socketUrl = "http://10.0.2.2:5000" // Example: Replace with your server IP and port
            socketManager.connect(socketUrl, userId, userName)
            Log.d("ChatViewModel", "Socket connection initiated.")

            socketManager.messageEvents.collect { event ->
                Log.d("ChatViewModel", "Received SocketEvent: ${event::class.simpleName}")
                when (event) {
                    is SocketEvent.Connected -> {
                        Log.d("ChatViewModel", "Socket Connected. Authenticating...")
                        // Authentication is handled by SocketManager.connect
                    }
                    is SocketEvent.Authenticated -> {
                        if (event.success) {
                            Log.d("ChatViewModel", "Authenticated successfully.")
                            // If a chat ID is already set, join it now that we are authenticated
                            currentChatId?.let { chatIdToJoin ->
                                Log.d("ChatViewModel", "Joining chat after authentication: $chatIdToJoin")
                                socketManager.joinChat(chatIdToJoin)
                            }
                        } else {
                            Log.e("ChatViewModel", "Authentication failed: ${event.message}")
                            _uiState.value = ChatUiState.Error("Authentication failed: ${event.message}")
                        }
                    }
                    is SocketEvent.JoinedChat -> {
                        Log.d("ChatViewModel", "Joined chat: ${event.chatName} (${event.chatId})")
                        // Initial messages will come via RecentMessages event
                    }
                    is SocketEvent.NewMessage -> {
                        Log.d("ChatViewModel", "New message received: ${event.messageJson}")
                        try {
                            val newMessage = gson.fromJson(event.messageJson, Message::class.java)
                            _messages.update { currentMessages ->
                                (currentMessages + newMessage).sortedBy { it.timestamp ?: 0L }
                            }
                            _uiState.value = ChatUiState.Success
                            Log.d("ChatViewModel", "UI State set to Success after new message")
                        } catch (e: Exception) {
                            Log.e("ChatViewModel", "Error parsing new message JSON: ${e.message}", e)
                            _uiState.value = ChatUiState.Error("Error al procesar nuevo mensaje: ${e.message}")
                        }
                    }
                    is SocketEvent.RecentMessages -> {
                        Log.d("ChatViewModel", "Recent messages received: ${event.messagesJson.size}")
                        try {
                            val recentMessages = event.messagesJson.map { messageJsonString ->
                                gson.fromJson(messageJsonString, Message::class.java)
                            }
                            _messages.update { recentMessages.sortedBy { it.timestamp ?: 0L } }
                            _uiState.value = ChatUiState.Success
                            Log.d("ChatViewModel", "UI State set to Success after recent messages")
                        } catch (e: Exception) {
                            Log.e("ChatViewModel", "Error parsing recent messages JSON: ${e.message}", e)
                            _uiState.value = ChatUiState.Error("Error al procesar mensajes recientes: ${e.message}")
                        }
                    }
                    is SocketEvent.Error -> {
                        Log.e("ChatViewModel", "Socket Error: ${event.message}")
                        _uiState.value = ChatUiState.Error("Socket Error: ${event.message}")
                    }
                    is SocketEvent.Disconnected -> {
                        Log.d("ChatViewModel", "Socket Disconnected.")
                        // Handle disconnection if needed
                    }
                    is SocketEvent.ChatUpdated -> { /* Ignore in this ViewModel */ }
                }
            }
        }
    }

    fun loadMessages(chatId: String) {
        currentChatId = chatId // Store current chat ID
        _uiState.value = ChatUiState.Loading // Always show loading when a new chat is loaded

        // If socket is already connected and authenticated, join chat immediately
        if (socketManager.socket?.connected() == true) {
            Log.d("ChatViewModel", "Socket connected and authenticated, joining chat: $chatId")
            socketManager.joinChat(chatId)
        } else {
            Log.d("ChatViewModel", "Socket not connected or not authenticated, waiting for connection/auth to join chat: $chatId")
            // The socket connection and authentication will happen in init block,
            // and then it will join the chat via the Authenticated event handler.
        }
    }

    fun sendMessage(chatId: String, text: String) {
        if (socketManager.socket?.connected() == true) {
            socketManager.sendMessage(chatId, text)
        } else {
            Log.e("ChatViewModel", "Socket not connected, cannot send message.")
            _uiState.value = ChatUiState.Error("No se pudo enviar el mensaje: Socket no conectado.")
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.disconnect()
        Log.d("ChatViewModel", "ViewModel cleared, socket disconnected.")
    }

    fun resetChatState() {
        currentChatId?.let { socketManager.leaveChat(it) }
        _messages.value = emptyList()
        _uiState.value = ChatUiState.Loading
        currentChatId = null
        Log.d("ChatViewModel", "Chat state has been reset.")
    }
}
