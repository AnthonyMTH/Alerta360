package com.unsa.alerta360.data.repository

import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.unsa.alerta360.data.local.UserData
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun saveUserDetails(userId: String, userData: UserData): Result<Unit> {
        return try {
            // Aseguramos que el UID del UserData coincida, o lo asignamos si no lo tiene
            val dataToSave = if (userData.uid.isEmpty() || userData.uid != userId) {
                userData.copy(uid = userId)
            } else {
                userData
            }
            firestore.collection("usuarios").document(userId).set(dataToSave).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Error al guardar los detalles del usuario: ${e.message}")
        }
    }

    override suspend fun getUserDetails(userId: String): Result<UserData?> {
        return try {
            val documentSnapshot = firestore.collection("usuarios").document(userId).get().await()
            val userData = documentSnapshot.toObject(UserData::class.java)
            Result.Success(userData)
        } catch (e: Exception) {
            Result.Error(e, "Error al obtener los detalles del usuario: ${e.message}")
        }
    }
}