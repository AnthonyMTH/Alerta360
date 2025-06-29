package com.unsa.alerta360.data.repository

import com.unsa.alerta360.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.unsa.alerta360.data.mapper.toDomain
import com.unsa.alerta360.data.model.UserDto
import com.unsa.alerta360.data.network.UserApiService
import kotlinx.coroutines.tasks.await
import javax.inject.Inject // Si usas Hilt
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.model.User

class AuthRepositoryImpl @Inject constructor( // Inyecta FirebaseAuth (Hilt ejemplo)
    private val firebaseAuth: FirebaseAuth,
    private val userApiService: UserApiService
) : AuthRepository {

    override suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Result.Success(it)
            } ?: Result.Error(Exception("Usuario no encontrado tras login exitoso."))
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.Error(e, "El correo electrónico no está registrado.")
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.Error(e, "La contraseña es incorrecta.")
        } catch (e: Exception) {
            Result.Error(e, "Error al iniciar sesión: ${e.message}")
        }
    }

    override suspend fun registerUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            authResult.user?.let {
                Result.Success(it)
            } ?: Result.Error(Exception("Usuario no creado tras registro exitoso."))
        } catch (e: Exception) {
            Result.Error(e, "Error en el registro: ${e.message}")
        }
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun logoutUser() {
        firebaseAuth.signOut()
    }

    override suspend fun saveUserDetails(uid: String, userData: UserDto): Result<UserDto> {
        return try {
            val response = userApiService.createUser(userData)
            if (response.isSuccessful) {
                response.body()?.let { user ->
                    Result.Success(user)
                } ?: Result.Error(Exception("Respuesta vacía del servidor"), "Error al procesar respuesta del servidor")
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                Result.Error(
                    Exception("Error HTTP ${response.code()}"),
                    "Error al guardar usuario: $errorMessage"
                )
            }
        } catch (e: Exception) {
            Result.Error(e, "Error de conexión al guardar usuario: ${e.message}")
        }
    }

    override suspend fun getUserDetails(uid: String): User {
            val response = userApiService.getUserById(uid)
            if (response.isSuccessful) {
                return response.body()?.toDomain()
                    ?: throw Exception("Usuario no encontrado")
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                throw Exception("Error al obtener usuario: $errorMessage")
            }
    }
}