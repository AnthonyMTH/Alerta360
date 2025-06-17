package com.unsa.alerta360.data.local

data class UserData(
    val uid: String = "",
    val nombres: String = "",
    val apellidos: String = "",
    val dni: String = "",
    val celular: String = "",
    val email: String = "",
    val direccion: String = "",
    val distrito: String = "",
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)
