package com.unsa.alerta360.data.repository

import com.unsa.alerta360.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject // Si usas Hilt
import com.unsa.alerta360.domain.model.Result

class AuthRepositoryImpl @Inject constructor( // Inyecta FirebaseAuth (Hilt ejemplo)
    private val firebaseAuth: FirebaseAuth
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
}