package com.unsa.alerta360.data.network

import com.unsa.alerta360.data.model.IncidentDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface IncidentApi {

    @GET("incident")
    suspend fun getAllIncidents(): Response<List<IncidentDto>>

    @POST("incident/create")
    suspend fun createIncident(@Body incident: IncidentDto): Response<IncidentDto>
    @GET("incident/{id}")
    suspend fun getIncident(@Path("id") id: String): Response<IncidentDto>
}