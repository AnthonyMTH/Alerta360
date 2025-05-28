package com.unsa.alerta360.domain.usecase.auth

import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser // O modelo de dominio User
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<FirebaseUser> { // O Result<User>
        if (email.isBlank() || password.isBlank()) {
            return Result.Error(IllegalArgumentException("Email y contraseña no pueden estar vacíos."), "Email y contraseña no pueden estar vacíos.")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return Result.Error(IllegalArgumentException("Formato de correo inválido."), "Formato de correo electrónico no válido.")
        }
        // Otras validaciones básicas pueden ir aquí o en el ViewModel
        return authRepository.loginUser(email, password)
    }
}