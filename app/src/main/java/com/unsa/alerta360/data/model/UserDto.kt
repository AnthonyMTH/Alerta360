package com.unsa.alerta360.data.model

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("_id")
    val uid: String,  // ID de firebase

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("phone_number")
    val phoneNumber: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("district")
    val district: String,

    @SerializedName("dni")
    val dni: String,

    // Campos de timestamp (solo para respuestas)
    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("__v")
    val version: Int? = null
)
