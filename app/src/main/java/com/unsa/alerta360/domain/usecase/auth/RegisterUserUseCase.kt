package com.unsa.alerta360.domain.usecase.auth

import com.unsa.alerta360.data.local.UserData
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.repository.AuthRepository
import com.unsa.alerta360.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseUser // O tu modelo de dominio User
import javax.inject.Inject

data class RegisterUserInput(
    val email: String,
    val contrasena: String,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val celular: String,
    val direccion: String,
    val distrito: String
)

class RegisterUserUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(input: RegisterUserInput): Result<FirebaseUser> { // O Result<User>
        // Validaciones
        if (input.nombres.isBlank() || input.apellidos.isBlank() || input.dni.isBlank() ||
            input.celular.isBlank() || input.email.isBlank() || input.contrasena.isBlank() ||
            input.direccion.isBlank() || input.distrito.isBlank()
        ) {
            return Result.Error(IllegalArgumentException("Todos los campos son requeridos."), "Por favor, completa todos los campos.")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input.email).matches()) {
            return Result.Error(IllegalArgumentException("Correo inválido."), "Correo electrónico no válido.")
        }
        if (input.contrasena.length < 6) {
            return Result.Error(IllegalArgumentException("Contraseña corta."), "La contraseña debe tener al menos 6 caracteres.")
        }

        return when (val authResult = authRepository.registerUser(input.email.trim(), input.contrasena)) {
            is Result.Success -> {
                val firebaseUser = authResult.data
                val userData = UserData(
                    uid = firebaseUser.uid,
                    nombres = input.nombres.trim(),
                    apellidos = input.apellidos.trim(),
                    dni = input.dni.trim(),
                    celular = input.celular.trim(),
                    email = input.email.trim(),
                    direccion = input.direccion.trim(),
                    distrito = input.distrito
                )
                // Guardar detalles adicionales del usuario
                when (val saveDetailsResult = userRepository.saveUserDetails(firebaseUser.uid, userData)) {
                    is Result.Success -> authResult // Devuelve el resultado del registro de Auth
                    is Result.Error -> Result.Error(saveDetailsResult.exception, "Usuario registrado, pero error al guardar detalles: ${saveDetailsResult.message}")
                }
            }
            is Result.Error -> authResult // Propaga el error de autenticación
        }
    }
}