package com.unsa.alerta360.domain.model

data class User(
    val uid: String,
    val email: String?,
    val displayName: String? = null // Podrías poblar esto desde UserData
)