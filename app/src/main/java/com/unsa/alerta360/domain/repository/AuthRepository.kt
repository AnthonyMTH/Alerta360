package com.unsa.alerta360.domain.repository

import com.unsa.alerta360.domain.model.Result
import com.google.firebase.auth.FirebaseUser // O tu modelo de dominio User
import com.unsa.alerta360.data.model.UserDto
import com.unsa.alerta360.domain.model.User

interface AuthRepository {
    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> // O Result<User>
    suspend fun registerUser(email: String, password: String): Result<FirebaseUser> // O Result<User>
    fun getCurrentUser(): FirebaseUser? // O User?
    suspend fun logoutUser()
    suspend fun saveUserDetails(uid: String, userData: UserDto): Result<UserDto>
    suspend fun getUserDetails(uid: String): User
}