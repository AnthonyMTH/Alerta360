package com.unsa.alerta360.domain.usecase.incident

import com.unsa.alerta360.data.repository.Resource
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.repository.IncidentRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAllIncidentsUseCase @Inject constructor(
    private val repository: IncidentRepository
) {
    operator fun invoke(): Flow<Resource<List<Incident>>> {
        return repository.observeAllIncidents()
    }
}