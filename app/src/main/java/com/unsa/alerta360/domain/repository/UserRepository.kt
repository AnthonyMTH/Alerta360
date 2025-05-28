package com.unsa.alerta360.domain.repository

import com.unsa.alerta360.data.local.UserData
import com.unsa.alerta360.domain.model.Result

interface UserRepository {
    suspend fun saveUserDetails(userId: String, userData: UserData): Result<Unit>
    suspend fun getUserDetails(userId: String): Result<UserData?>
}