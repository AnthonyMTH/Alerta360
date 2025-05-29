package com.unsa.alerta360.data.local

data class Incident(
    val title: String,
    val username: String,
    val location: String,
    val address: String,
    val description: String,
    val district: String,
    val type: String
)