package com.unsa.alerta360.data.model

import androidx.annotation.Nullable
import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("_id")
    val uid: String? = null,  // ID de firebase

    @SerializedName("first_name")
    val firstName: String? = null,

    @SerializedName("last_name")
    val lastName: String? = null,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("phone_number")
    val phoneNumber: String? = null,

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("district")
    val district: String? = null,

    @SerializedName("dni")
    val dni: String? = null,

    // Campos de timestamp (solo para respuestas)
    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null,

    @SerializedName("__v")
    val version: Int? = null
)
