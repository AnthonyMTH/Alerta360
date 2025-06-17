package com.unsa.alerta360.domain.usecase.incident

import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.repository.IncidentRepository
import javax.inject.Inject

class GetAllIncidentsUseCase @Inject constructor(
    private val repo: IncidentRepository
) {
    suspend operator fun invoke(): List<Incident> = repo.getAllIncidents()
}