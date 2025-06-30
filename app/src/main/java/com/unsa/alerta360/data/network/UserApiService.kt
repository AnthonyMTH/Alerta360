package com.unsa.alerta360.data.network

import com.unsa.alerta360.data.model.UserDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {
    @POST("user/create")
    suspend fun createUser(
        @Body user: UserDto
    ): Response<UserDto>

    @GET("user/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<UserDto>

//    @GET("users/{firebaseUid}")
//    suspend fun getUserByFirebaseUid(
//        @Path("firebaseUid") firebaseUid: String
//    ): Response<UserDto>

    @PUT("user/update/{id}")
    suspend fun updateUser(
        @Path("id") id: String,
        @Body user: UserDto
    ): Response<UserDto>
}