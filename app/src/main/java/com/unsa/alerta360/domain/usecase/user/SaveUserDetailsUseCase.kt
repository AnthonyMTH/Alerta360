package com.unsa.alerta360.domain.usecase.user

import com.unsa.alerta360.data.local.UserData
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserDetailsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String, userData: UserData): Result<Unit> {
        return userRepository.saveUserDetails(userId, userData)
    }
}