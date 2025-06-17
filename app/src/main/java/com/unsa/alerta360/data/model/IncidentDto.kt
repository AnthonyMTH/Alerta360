package com.unsa.alerta360.data.model

import com.google.gson.annotations.SerializedName

data class IncidentDto(
    @SerializedName("_id")
    val _id: String? = null,
    
    @SerializedName("description")
    val description: String,
    
    @SerializedName("incidentType")
    val incidentType: String,
    
    @SerializedName("ubication")
    val ubication: String,
    
    @SerializedName("geolocation")
    val geolocation: String,

    @SerializedName("district")
    val district: String,
    
    @SerializedName("evidence")
    val evidence: List<String> = emptyList(),
    
    @SerializedName("user_id")
    val user_id: String,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("createdAt")
    val createdAt: String? = null,
    
    @SerializedName("updatedAt")
    val updatedAt: String? = null,
    
    @SerializedName("__v")
    val __v: Int? = null
)