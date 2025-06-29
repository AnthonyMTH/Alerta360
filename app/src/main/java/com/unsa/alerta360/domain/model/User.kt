package com.unsa.alerta360.domain.model

data class User(
    val _id: String? = null,  // ID de firebase
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
    val password: String? = null,
    val district: String? = null,
    val dni: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val __v: Int? = null
)