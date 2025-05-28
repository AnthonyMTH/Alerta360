package com.unsa.alerta360.data.local

data class UserData(
    val uid: String = "", // Para guardar el UID también en el documento, puede ser útil
    val nombres: String = "",
    val apellidos: String = "",
    val dni: String = "",
    val celular: String = "",
    val email: String = "",
    val direccion: String = "",
    val distrito: String = "",
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now() // Fecha de creación
)