package com.unsa.alerta360.data.network.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.json.JSONObject
import kotlinx.coroutines.*

class SocketManager {

    var socket: Socket? = null
    private val _messageEvents = MutableSharedFlow<SocketEvent>(replay = 1)
    val messageEvents = _messageEvents.asSharedFlow()

    private var _isAuthenticated: Boolean = false
    private var _isConnected: Boolean = false
    private var authJob: Job? = null
    private var connectionJob: Job? = null

    fun isAuthenticated(): Boolean = _isAuthenticated
    fun isConnected(): Boolean = _isConnected

    fun connect(url: String, userId: String, userName: String) {
        // Cancelar cualquier intento de conexión existente
        connectionJob?.cancel()
        authJob?.cancel()

        connectionJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val options = IO.Options().apply {
                    forceNew = true
                    reconnection = true
                    reconnectionAttempts = 5
                    reconnectionDelay = 1000
                    timeout = 15000
                }

                socket = IO.socket(url, options)

                socket?.on(Socket.EVENT_CONNECT, onConnect)
                socket?.on(Socket.EVENT_DISCONNECT, onDisconnect)
                socket?.on(Socket.EVENT_CONNECT_ERROR, onConnectError)
                socket?.on("authenticated", onAuthenticated)
                socket?.on("joined_chat", onJoinedChat)
                socket?.on("new_message", onNewMessage)
                socket?.on("recent_messages", onRecentMessages)
                socket?.on("error", onError)

                socket?.connect()

                Log.d("SocketManager", "Attempting to connect to $url with userId: $userId")

                // Esperar conexión con tiempo de espera
                var attempts = 0
                val maxAttempts = 30 // 15 seconds total (30 * 500ms)

                while (!_isConnected && attempts < maxAttempts) {
                    delay(500)
                    attempts++
                }

                if (_isConnected) {
                    authenticateUser(userId, userName)
                } else {
                    Log.e("SocketManager", "Connection timeout after 15 seconds")
                    _messageEvents.tryEmit(SocketEvent.Error("Timeout de conexión"))
                }

            } catch (e: Exception) {
                Log.e("SocketManager", "Error connecting to socket: ${e.message}", e)
                _messageEvents.tryEmit(SocketEvent.Error("Error de conexión: ${e.message}"))
            }
        }
    }

    private fun authenticateUser(userId: String, userName: String) {
        authJob?.cancel()
        authJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                socket?.emit("authenticate", JSONObject().apply {
                    put("userId", userId)
                    put("userName", userName)
                })

                Log.d("SocketManager", "Authentication request sent for userId: $userId")

                var attempts = 0
                val maxAttempts = 20 // 10 seconds total (20 * 500ms)

                while (!_isAuthenticated && _isConnected && attempts < maxAttempts) {
                    delay(500)
                    attempts++
                }

                if (!_isAuthenticated && _isConnected) {
                    Log.e("SocketManager", "Authentication timeout")
                    _messageEvents.tryEmit(SocketEvent.Error("Timeout de autenticación"))
                }

            } catch (e: Exception) {
                Log.e("SocketManager", "Error during authentication: ${e.message}", e)
                _messageEvents.tryEmit(SocketEvent.Error("Error de autenticación: ${e.message}"))
            }
        }
    }

    fun disconnect() {
        connectionJob?.cancel()
        authJob?.cancel()

        socket?.disconnect()
        socket?.off(Socket.EVENT_CONNECT, onConnect)
        socket?.off(Socket.EVENT_DISCONNECT, onDisconnect)
        socket?.off(Socket.EVENT_CONNECT_ERROR, onConnectError)
        socket?.off("authenticated", onAuthenticated)
        socket?.off("joined_chat", onJoinedChat)
        socket?.off("new_message", onNewMessage)
        socket?.off("recent_messages", onRecentMessages)
        socket?.off("error", onError)
        socket = null

        _isConnected = false
        _isAuthenticated = false

        Log.d("SocketManager", "Socket disconnected.")
    }

    fun joinChat(chatId: String) {
        if (!_isAuthenticated) {
            Log.e("SocketManager", "Cannot join chat: not authenticated")
            _messageEvents.tryEmit(SocketEvent.Error("No autenticado para unirse al chat"))
            return
        }

        socket?.emit("join_chat", JSONObject().apply {
            put("chatId", chatId)
        })
        Log.d("SocketManager", "Attempting to join chat: $chatId")
    }

    fun leaveChat(chatId: String) {
        if (!_isAuthenticated) {
            Log.w("SocketManager", "Cannot leave chat: not authenticated")
            return
        }

        socket?.emit("leave_chat", JSONObject().apply {
            put("chatId", chatId)
        })
        Log.d("SocketManager", "Attempting to leave chat: $chatId")
    }

    fun sendMessage(chatId: String, text: String) {
        if (!_isAuthenticated) {
            Log.e("SocketManager", "Cannot send message: not authenticated")
            _messageEvents.tryEmit(SocketEvent.Error("No autenticado para enviar mensaje"))
            return
        }

        socket?.emit("send_message", JSONObject().apply {
            put("chatId", chatId)
            put("text", text)
        })
        Log.d("SocketManager", "Attempting to send message to chat $chatId: $text")
    }

    private val onConnect = Emitter.Listener {
        Log.d("SocketManager", "Socket Connected!")
        _isConnected = true
        _messageEvents.tryEmit(SocketEvent.Connected)
    }

    private val onDisconnect = Emitter.Listener {
        Log.d("SocketManager", "Socket Disconnected!")
        _isAuthenticated = false
        _isConnected = false
        _messageEvents.tryEmit(SocketEvent.Disconnected)
    }

    private val onConnectError = Emitter.Listener { args ->
        val error = if (args.isNotEmpty() && args[0] is Exception) {
            (args[0] as Exception).message
        } else {
            args.joinToString()
        }
        Log.e("SocketManager", "Socket Connect Error: $error")
        _isAuthenticated = false
        _isConnected = false
        _messageEvents.tryEmit(SocketEvent.Error("Error de conexión: $error"))
    }

    private val onAuthenticated = Emitter.Listener { args ->
        val data = args[0] as? JSONObject
        val success = data?.optBoolean("success") ?: false
        _isAuthenticated = success
        Log.d("SocketManager", "Authenticated: $data")
        _messageEvents.tryEmit(SocketEvent.Authenticated(success, data?.optString("message") ?: "Unknown"))
    }

    private val onJoinedChat = Emitter.Listener { args ->
        val data = args[0] as? JSONObject
        Log.d("SocketManager", "Joined Chat: $data")
        _messageEvents.tryEmit(SocketEvent.JoinedChat(data?.optString("chatId") ?: "", data?.optString("chatName") ?: ""))
    }

    private val onNewMessage = Emitter.Listener { args ->
        val data = args[0] as? JSONObject
        Log.d("SocketManager", "New Message: $data")
        if (data == null) {
            Log.e("SocketManager", "New Message data is null or not JSONObject")
            return@Listener
        }
        val messageJson = data.optJSONObject("message")
        if (messageJson == null) {
            Log.e("SocketManager", "New Message 'message' field is null or not JSONObject: $data")
            return@Listener
        }
        _messageEvents.tryEmit(SocketEvent.NewMessage(messageJson.toString()))
    }

    private val onRecentMessages = Emitter.Listener { args ->
        val data = args[0] as? JSONObject
        Log.d("SocketManager", "Recent Messages: $data")
        if (data == null) {
            Log.e("SocketManager", "Recent Messages data is null or not JSONObject")
            return@Listener
        }
        val chatId = data.optString("chatId")
        if (chatId.isNullOrEmpty()) {
            Log.e("SocketManager", "Recent Messages chatId is null or empty: $data")
            return@Listener
        }
        val messagesArray = data.optJSONArray("messages")
        if (messagesArray == null) {
            Log.e("SocketManager", "Recent Messages 'messages' field is null or not JSONArray: $data")
            return@Listener
        }
        val messagesList = mutableListOf<String>()
        for (i in 0 until messagesArray.length()) {
            messagesList.add(messagesArray.getJSONObject(i).toString())
        }
        _messageEvents.tryEmit(SocketEvent.RecentMessages(chatId, messagesList))
        Log.d("SocketManager", "RecentMessages event sent to Channel.")
    }

    private val onError = Emitter.Listener { args ->
        val data = args[0] as? JSONObject
        val errorMessage = data?.optString("message") ?: "Unknown error"
        Log.e("SocketManager", "Socket Error: $errorMessage")
        _messageEvents.tryEmit(SocketEvent.Error(errorMessage))
    }

    private val onChatUpdated = Emitter.Listener { args ->
        val data = args[0] as? JSONObject
        Log.d("SocketManager", "Chat Updated: $data")
        data?.let {
            val chatId = it.optString("chatId")
            val lastMessageJson = it.optJSONObject("lastMessage")?.toString()
            val messageCount = it.optInt("messageCount")
            _messageEvents.tryEmit(SocketEvent.ChatUpdated(chatId, lastMessageJson, messageCount))
        }
    }
}

sealed class SocketEvent {
    object Connected : SocketEvent()
    object Disconnected : SocketEvent()
    data class Authenticated(val success: Boolean, val message: String) : SocketEvent()
    data class JoinedChat(val chatId: String, val chatName: String) : SocketEvent()
    data class NewMessage(val messageJson: String) : SocketEvent()
    data class RecentMessages(val chatId: String, val messagesJson: List<String>) : SocketEvent()
    data class Error(val message: String) : SocketEvent()
    data class ChatUpdated(val chatId: String, val lastMessageJson: String?, val messageCount: Int) : SocketEvent()
}