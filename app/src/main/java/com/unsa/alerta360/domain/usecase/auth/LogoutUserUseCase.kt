package com.unsa.alerta360.domain.usecase.auth

import com.unsa.alerta360.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser // O tu modelo de dominio User
import javax.inject.Inject

class LogoutUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Unit {
        return authRepository.logoutUser()
    }
}