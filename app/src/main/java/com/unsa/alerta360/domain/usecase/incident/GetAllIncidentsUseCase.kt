package com.unsa.alerta360.domain.usecase.incident

import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.repository.IncidentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllIncidentsUseCase @Inject constructor(
    private val repo: IncidentRepository
) {
    operator fun invoke(): Flow<List<Incident>> =
        repo.observeIncidents()
}