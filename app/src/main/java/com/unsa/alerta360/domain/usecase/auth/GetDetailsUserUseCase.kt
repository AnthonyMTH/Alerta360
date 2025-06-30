package com.unsa.alerta360.domain.usecase.auth

import com.google.firebase.auth.FirebaseUser
import com.unsa.alerta360.domain.model.User
import com.unsa.alerta360.domain.repository.AuthRepository
import javax.inject.Inject

class GetDetailsUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(id: String): User {
        return authRepository.getUserDetails(id)
    }
}