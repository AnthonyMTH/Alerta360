package com.unsa.alerta360.presentation.incident

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.State
import androidx.lifecycle.viewModelScope
import com.unsa.alerta360.domain.model.Account
import com.unsa.alerta360.domain.model.Incident
import com.unsa.alerta360.domain.usecase.account.GetAccountDetailsUseCase
import com.unsa.alerta360.domain.usecase.incident.GetIncidentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.unsa.alerta360.domain.model.Result

@HiltViewModel
class IncidentViewModel @Inject constructor(
    private val getIncidentUseCase: GetIncidentUseCase,
    private val getAccountDetailsUseCase: GetAccountDetailsUseCase
) : ViewModel() {

    private val _currentIncident = mutableStateOf<Incident?>(null)
    val currentIncident: State<Incident?> = _currentIncident

    private val _accountData = mutableStateOf<Account?>(null)
    val accountData: State<Account?> = _accountData

    fun loadIncidentById(id: String) {
        viewModelScope.launch {
            val incident = getIncidentUseCase(id)
            _currentIncident.value = incident

            incident?.user_id?.let { userId ->
                when (val result = getAccountDetailsUseCase(userId)) {
                    is Result.Success -> _accountData.value = result.data
                    is Result.Error -> {
                        _accountData.value = null
                        Log.e("IncidentViewModel", "Error al obtener la cuenta: ${result.message}", result.exception)
                    }
                }
            }
        }
    }
}