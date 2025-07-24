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
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

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
    private val socketManager: SocketManager
) : ViewModel() {

    private val gson: Gson = GsonBuilder().create()

    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    val userId = getCurrentUserUseCase()!!.uid
    private var currentChatId: String? = null
    private var socketInitializationJob: Job? = null
    private var loadingTimeoutJob: Job? = null
    private var isSocketInitialized = false

    init {
        initializeSocket()
    }

    private fun initializeSocket() {
        socketInitializationJob?.cancel()
        socketInitializationJob = viewModelScope.launch {
            try {
                val userDetails = getDetailsUserUseCase(userId)
                val userName = userDetails.first_name ?: "Usuario"

                //val socketUrl = "http://10.0.2.2:5000"
                val socketUrl = "https://backend-alerta360.onrender.com"

                socketManager.connect(socketUrl, userId, userName)
                Log.d("ChatViewModel", "Socket connection initiated.")

                socketManager.messageEvents.collect { event ->
                    Log.d("ChatViewModel", "Received SocketEvent: ${event::class.simpleName}")
                    handleSocketEvent(event)
                }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error initializing socket: ${e.message}", e)
                _uiState.value = ChatUiState.Error("Error de inicialización: ${e.message}")
            }
        }
    }

    private fun handleSocketEvent(event: SocketEvent) {
        when (event) {
            is SocketEvent.Connected -> {
                Log.d("ChatViewModel", "Socket Connected. Waiting for authentication...")
            }
            is SocketEvent.Authenticated -> {
                if (event.success) {
                    Log.d("ChatViewModel", "Authenticated successfully.")
                    isSocketInitialized = true

                    currentChatId?.let { chatIdToJoin ->
                        Log.d("ChatViewModel", "Joining chat after authentication: $chatIdToJoin")
                        socketManager.joinChat(chatIdToJoin)
                        startLoadingTimeout()
                    }
                } else {
                    Log.e("ChatViewModel", "Authentication failed: ${event.message}")
                    _uiState.value = ChatUiState.Error("Authentication failed: ${event.message}")
                }
            }
            is SocketEvent.JoinedChat -> {
                Log.d("ChatViewModel", "Joined chat: ${event.chatName} (${event.chatId})")
            }
            is SocketEvent.NewMessage -> {
                Log.d("ChatViewModel", "New message received: ${event.messageJson}")
                try {
                    val newMessage = gson.fromJson(event.messageJson, Message::class.java)
                    _messages.update { currentMessages ->
                        if (currentMessages.any { it.id == newMessage.id }) {
                            currentMessages // No agregar si ya existe
                        } else {
                            (currentMessages + newMessage).sortedBy { it.timestamp ?: 0L }
                        }
                    }
                    // Only set to Success if we're not already in Success state
                    if (_uiState.value !is ChatUiState.Success) {
                        _uiState.value = ChatUiState.Success
                        cancelLoadingTimeout()
                        Log.d("ChatViewModel", "UI State set to Success after new message")
                    }
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Error parsing new message JSON: ${e.message}", e)
                    _uiState.value = ChatUiState.Error("Error al procesar nuevo mensaje: ${e.message}")
                    cancelLoadingTimeout()
                }
            }
            is SocketEvent.RecentMessages -> {
                if (event.chatId == currentChatId) {
                    Log.d("ChatViewModel", "Recent messages for current chat received: ${event.messagesJson.size}")
                    try {
                        val recentMessages = event.messagesJson.map { messageJsonString ->
                            gson.fromJson(messageJsonString, Message::class.java)
                        }
                        _messages.update { recentMessages.sortedBy { it.timestamp ?: 0L } }
                        _uiState.value = ChatUiState.Success
                        cancelLoadingTimeout()
                        Log.d("ChatViewModel", "UI State set to Success after recent messages")
                    } catch (e: Exception) {
                        Log.e("ChatViewModel", "Error parsing recent messages JSON: ${e.message}", e)
                        _uiState.value = ChatUiState.Error("Error al procesar mensajes recientes: ${e.message}")
                        cancelLoadingTimeout()
                    }
                } else {
                    Log.d("ChatViewModel", "Ignoring recent messages for old chat: ${event.chatId}")
                }
            }
            is SocketEvent.Error -> {
                Log.e("ChatViewModel", "Socket Error: ${event.message}")
                _uiState.value = ChatUiState.Error("Socket Error: ${event.message}")
                cancelLoadingTimeout()
            }
            is SocketEvent.Disconnected -> {
                Log.d("ChatViewModel", "Socket Disconnected.")
                isSocketInitialized = false
                // Try to reconnect
                initializeSocket()
            }
            is SocketEvent.ChatUpdated -> {
                Log.d("ChatViewModel", "Chat Updated event received")
            }
        }
    }

    fun loadMessages(chatId: String) {
        Log.d("ChatViewModel", "Loading messages for chatId: $chatId")

        cancelLoadingTimeout()

        // Dejar current chat si es diferente
        if (currentChatId != null && currentChatId != chatId) {
            socketManager.leaveChat(currentChatId!!)
        }

        currentChatId = chatId
        _messages.value = emptyList()
        _uiState.value = ChatUiState.Loading

        if (isSocketInitialized && socketManager.isAuthenticated()) {
            Log.d("ChatViewModel", "Socket already authenticated, joining chat: $chatId")
            socketManager.joinChat(chatId)
            startLoadingTimeout()
        } else if (socketManager.isConnected() && !socketManager.isAuthenticated()) {
            Log.d("ChatViewModel", "Socket connected but not authenticated, waiting for auth event to join chat: $chatId")
            // Se unirá al chat en el controlador de éxito de autenticación
        } else {
            Log.d("ChatViewModel", "Socket not connected, initializing connection for chat: $chatId")
            // Se inicializará el socket y se unirá al chat después de la autenticación
            if (!isSocketInitialized) {
                initializeSocket()
            }
            startLoadingTimeout()
        }
    }

    private fun startLoadingTimeout() {
        loadingTimeoutJob?.cancel()
        loadingTimeoutJob = viewModelScope.launch {
            delay(15000) // 15 seconds timeout
            if (_uiState.value is ChatUiState.Loading) {
                Log.e("ChatViewModel", "Loading timeout - no messages received")
                _uiState.value = ChatUiState.Error("Timeout: No se pudieron cargar los mensajes. Intenta nuevamente.")
            }
        }
    }

    private fun cancelLoadingTimeout() {
        loadingTimeoutJob?.cancel()
        loadingTimeoutJob = null
    }

    fun sendMessage(chatId: String, text: String) {
        if (text.isBlank()) {
            Log.w("ChatViewModel", "Attempted to send blank message")
            return
        }

        if (socketManager.isConnected() && socketManager.isAuthenticated()) {
            socketManager.sendMessage(chatId, text)
        } else {
            Log.e("ChatViewModel", "Socket not connected or authenticated, cannot send message.")
            _uiState.value = ChatUiState.Error("No se pudo enviar el mensaje: Socket no conectado.")
        }
    }

    fun retryConnection() {
        Log.d("ChatViewModel", "Retrying connection...")
        _uiState.value = ChatUiState.Loading
        socketManager.disconnect()
        isSocketInitialized = false
        initializeSocket()

        currentChatId?.let { chatId ->
            viewModelScope.launch {
                delay(2000)
                loadMessages(chatId)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cancelLoadingTimeout()
        socketInitializationJob?.cancel()
        socketManager.disconnect()
        Log.d("ChatViewModel", "ViewModel cleared, socket disconnected.")
    }

    fun resetChatState() {
        Log.d("ChatViewModel", "Resetting chat state...")
        cancelLoadingTimeout()
        viewModelScope.launch {
            try {
                currentChatId?.let { socketManager.leaveChat(it) }
                _messages.value = emptyList()
                _uiState.value = ChatUiState.Loading
                currentChatId = null
                Log.d("ChatViewModel", "Chat state has been reset.")
            } catch (e: CancellationException) {
                // Ignorar errores de cancelación durante el reseteo
                Log.d("ChatViewModel", "Reset cancelled normally")
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error during reset: ${e.message}")
            }
        }
    }
}