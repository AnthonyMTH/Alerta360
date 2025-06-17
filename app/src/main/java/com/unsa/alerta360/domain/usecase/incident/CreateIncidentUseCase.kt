package com.unsa.alerta360.domain.usecase.incident

import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.repository.IncidentRepository
import javax.inject.Inject


class CreateIncidentUseCase @Inject constructor(
    private val repository: IncidentRepository
) {
    suspend operator fun invoke(incident: Incident): Incident? {
        return repository.createIncident(incident)
    }
}