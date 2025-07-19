package com.unsa.alerta360.presentation.chatList

import androidx.lifecycle.ViewModel
import com.unsa.alerta360.domain.model.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ChatListViewModel : ViewModel() {

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats

    init {
        loadChats()
    }

    private fun loadChats() {
        _chats.value = listOf(
            Chat("1", "Alto Selva Alegre", "Chat para Alto Selva Alegre"),
            Chat("2", "Arequipa", "Chat para Arequipa"),
            Chat("3", "Cayma", "Chat para Cayma"),
            Chat("4", "Cerro Colorado", "Chat para Cerro Colorado"),
            Chat("5", "Characato", "Chat para Characato"),
            Chat("6", "Chiguata", "Chat para Chiguata"),
            Chat("7", "Jacobo Hunter", "Chat para Jacobo Hunter"),
            Chat("8", "Jose Luis Bustamante y Rivero", "Chat para Jose Luis Bustamante y Rivero"),
            Chat("9", "La Joya", "Chat para La Joya"),
            Chat("10", "Mariano Melgar", "Chat para Mariano Melgar"),
            Chat("11", "Miraflores", "Chat para Miraflores"),
            Chat("12", "Mollebaya", "Chat para Mollebaya"),
            Chat("13", "Paucarpata", "Chat para Paucarpata"),
            Chat("14", "Pocsi", "Chat para Pocsi"),
            Chat("15", "Polobaya", "Chat para Polobaya"),
            Chat("16", "Quequeña", "Chat para Quequeña"),
            Chat("17", "Sabandia", "Chat para Sabandia"),
            Chat("18", "Sachaca", "Chat para Sachaca"),
            Chat("19", "San Juan de Siguas", "Chat para San Juan de Siguas"),
            Chat("20", "San Juan de Tarucani", "Chat para San Juan de Tarucani"),
            Chat("21", "Santa Isabel de Siguas", "Chat para Santa Isabel de Siguas"),
            Chat("22", "Santa Rita de Siguas", "Chat para Santa Rita de Siguas"),
            Chat("23", "Socabaya", "Chat para Socabaya"),
            Chat("24", "Tiabaya", "Chat para Tiabaya"),
            Chat("25", "Uchumayo", "Chat para Uchumayo"),
            Chat("26", "Vitor", "Chat para Vitor"),
            Chat("27", "Yanahuara", "Chat para Yanahuara"),
            Chat("28", "Yarabamba", "Chat para Yarabamba"),
            Chat("29", "Yura", "Chat para Yura")
        )
    }
}