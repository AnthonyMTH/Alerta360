package com.unsa.alerta360.domain.usecase.auth

import com.unsa.alerta360.data.model.UserDto
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.model.User
import com.unsa.alerta360.domain.repository.AuthRepository
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(uid: String, userData: User): Result<UserDto> {
        return authRepository.updateUserDetails(uid, userData)
    }
}