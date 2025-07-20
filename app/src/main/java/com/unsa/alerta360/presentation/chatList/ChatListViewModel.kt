package com.unsa.alerta360.presentation.chatList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.unsa.alerta360.data.network.socket.SocketEvent
import com.unsa.alerta360.data.network.socket.SocketManager
import com.unsa.alerta360.data.util.DateLongAdapter
import com.unsa.alerta360.domain.model.Chat
import com.unsa.alerta360.domain.model.LastMessage
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.usecase.auth.GetCurrentUserUseCase
import com.unsa.alerta360.domain.usecase.auth.GetDetailsUserUseCase
import com.unsa.alerta360.domain.usecase.chat.GetAllChatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ChatListUiState {
    object Loading : ChatListUiState()
    data class Success(val chats: List<Chat>) : ChatListUiState()
    data class Error(val message: String) : ChatListUiState()
}

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getAllChatsUseCase: GetAllChatsUseCase,
    private val socketManager: SocketManager,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getDetailsUserUseCase: GetDetailsUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ChatListUiState>(ChatListUiState.Loading)
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    private val gson = GsonBuilder()
        .registerTypeAdapter(Long::class.java, DateLongAdapter())
        .registerTypeAdapter(java.lang.Long::class.java, DateLongAdapter())
        .create()

    init {
        loadChats()
        viewModelScope.launch {
            val userId = getCurrentUserUseCase()!!.uid
            val userDetails = getDetailsUserUseCase(userId)
            val userName = userDetails.first_name ?: "Usuario" // Default if null

            // TODO: Replace with your actual backend WebSocket URL
            val socketUrl = "http://10.0.2.2:5000" // Example: Replace with your server IP and port
            socketManager.connect(socketUrl, userId, userName)

            socketManager.messageEvents.collect { event ->
                when (event) {
                    is SocketEvent.ChatUpdated -> {
                        Log.d("ChatListViewModel", "Chat updated event received: ${event.chatId}")
                        _uiState.update { currentState ->
                            if (currentState is ChatListUiState.Success) {
                                val updatedChats = currentState.chats.map { chat ->
                                    if (chat.id == event.chatId) {
                                        val updatedLastMessage = event.lastMessageJson?.let {
                                            gson.fromJson(it, LastMessage::class.java)
                                        }
                                        chat.copy(
                                            lastMessage = updatedLastMessage,
                                            messageCount = event.messageCount
                                        )
                                    } else {
                                        chat
                                    }
                                }
                                ChatListUiState.Success(updatedChats)
                            } else {
                                currentState
                            }
                        }
                    }
                    else -> { /* Ignore other socket events in this ViewModel */ }
                }
            }
        }
    }

    fun loadChats() {
        viewModelScope.launch {
            _uiState.value = ChatListUiState.Loading
            when (val result = getAllChatsUseCase()) {
                is Result.Success -> _uiState.value = ChatListUiState.Success(result.data)
                is Result.Error -> _uiState.value = ChatListUiState.Error(result.message ?: "Error desconocido")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.disconnect()
        Log.d("ChatListViewModel", "ViewModel cleared, socket disconnected.")
    }
}
