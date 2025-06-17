package com.unsa.alerta360.data.network

import com.unsa.alerta360.data.model.AccountDTO
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Body

interface AccountApiService {

    @GET("user/{id}")
    suspend fun getAccountById(@Path("id") id: String): AccountDTO

    @PUT("user/{id}")
    suspend fun updateAccount(
        @Path("id") id: String,
        @Body userData: AccountDTO
    ): AccountDTO
}
