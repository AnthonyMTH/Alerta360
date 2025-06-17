package com.unsa.alerta360.domain.usecase.incident

import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.repository.IncidentRepository
import javax.inject.Inject

class GetIncidentUseCase @Inject constructor(
    private val repo: IncidentRepository
) {
    suspend operator fun invoke(id: String): Incident? = repo.getIncident(id)
}