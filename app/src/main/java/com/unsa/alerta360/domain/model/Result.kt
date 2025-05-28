package com.unsa.alerta360.domain.model

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception, val message: String? = null) : Result<Nothing>()
    // Puedes añadir un estado Loading aquí si los casos de uso lo necesitan
    // object Loading : Result<Nothing>()
}