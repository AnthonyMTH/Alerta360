package com.unsa.alerta360.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.unsa.alerta360.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await // Para usar await en Task

// Define un estado para el resultado del registro
sealed class RegistrationResult {
    object Idle : RegistrationResult()
    object Loading : RegistrationResult()
    object Success : RegistrationResult()
    data class Error(val message: String) : RegistrationResult()
}

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val db: FirebaseFirestore = Firebase.firestore

    private val _registrationState = MutableStateFlow<RegistrationResult>(RegistrationResult.Idle)
    val registrationState: StateFlow<RegistrationResult> = _registrationState

    fun registerUser(
        email: String,
        password: String,
        nombres: String,
        apellidos: String,
        dni: String,
        celular: String,
        direccion: String,
        distrito: String
    ) {
        _registrationState.value = RegistrationResult.Loading
        viewModelScope.launch {
            try {
                // Crear usuario en Firebase Auth
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    // Preparar los datos del perfil
                    val userProfile = UserProfile(
                        uid = firebaseUser.uid,
                        nombres = nombres,
                        apellidos = apellidos,
                        dni = dni,
                        celular = celular,
                        email = email, // Guardamos el email también en Firestore
                        direccion = direccion,
                        distrito = distrito
                    )

                    // Guardar el perfil en Cloud Firestore (BD)
                    // Usamos el UID del usuario como ID del documento en la colección "users"
                    db.collection("users").document(firebaseUser.uid)
                        .set(userProfile)
                        .await() // Espera a que se complete la escritura

                    _registrationState.value = RegistrationResult.Success
                } else {
                    _registrationState.value = RegistrationResult.Error("Error al crear el usuario (usuario nulo).")
                }

            } catch (e: Exception) {
                // Manejar errores específicos de Firebase si es necesario
                _registrationState.value = RegistrationResult.Error(e.message ?: "Error desconocido durante el registro.")
            }
        }
    }

    // Función para resetear el estado si es necesario (ej. al salir de la pantalla o reintentar)
    fun resetRegistrationState() {
        _registrationState.value = RegistrationResult.Idle
    }
}