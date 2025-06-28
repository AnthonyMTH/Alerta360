package com.unsa.alerta360.data.network

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Si ya tiene Authorization header, continuar
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest)
        }

        // Si no hay usuario autenticado, continuar sin token
        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            return chain.proceed(originalRequest)
        }

        // Agregar token para usuarios autenticados
        return try {
            val token = runBlocking {
                currentUser.getIdToken(false).await().token
            }

            if (token != null) {
                val authenticatedRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                chain.proceed(authenticatedRequest)
            } else {
                chain.proceed(originalRequest)
            }
        } catch (e: Exception) {
            // Si hay error obteniendo el token, continuar sin él
            chain.proceed(originalRequest)
        }
    }
}