package com.unsa.alerta360.domain.usecase.auth

import com.unsa.alerta360.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser // O tu modelo de dominio User
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }
}