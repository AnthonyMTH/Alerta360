package com.unsa.alerta360.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FcmApiService {

    @PUT("user/{userId}/fcm-token")
    suspend fun updateFcmToken(
        @Path("userId") userId: String,
        @Body tokenData: Map<String, String>
    ): Response<Unit>

    @PUT("user/{userId}/notification-preferences")
    suspend fun updateNotificationPreferences(
        @Path("userId") userId: String,
        @Body preferences: Map<String, Boolean>
    ): Response<Unit>

    @POST("user/{userId}/subscribe-location")
    suspend fun subscribeToLocation(
        @Path("userId") userId: String,
        @Body locationData: Map<String, String>
    ): Response<Unit>
}