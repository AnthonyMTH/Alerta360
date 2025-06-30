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
import com.unsa.alerta360.domain.model.User
import com.unsa.alerta360.domain.usecase.auth.GetCurrentUserUseCase
import com.unsa.alerta360.domain.usecase.auth.GetDetailsUserUseCase
import com.unsa.alerta360.domain.usecase.incident.DeleteIncidentUseCase

@HiltViewModel
class IncidentViewModel @Inject constructor(
    private val getIncidentUseCase: GetIncidentUseCase,
    private val getDetailsUserUseCase: GetDetailsUserUseCase,
    private val deleteIncidentUseCase: DeleteIncidentUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _currentIncident = mutableStateOf<Incident?>(null)
    val currentIncident: State<Incident?> = _currentIncident

    private val _accountData = mutableStateOf<User?>(User())
    val accountData: State<User?> = _accountData

    private val _isDeleting = mutableStateOf(false)
    val isDeleting: State<Boolean> = _isDeleting

    private val _deleteSuccess = mutableStateOf(false)
    val deleteSuccess: State<Boolean> = _deleteSuccess

    private val _deleteError = mutableStateOf<String?>(null)
    val deleteError: State<String?> = _deleteError

    fun loadIncidentById(id: String) {
        viewModelScope.launch {
            try {
                val incident = getIncidentUseCase(id)
                _currentIncident.value = incident

                incident?.user_id?.let { userId ->
                    try {
                        val user = getDetailsUserUseCase(userId)
                        _accountData.value = user
                        Log.d("IncidentViewModel", "Usuario cargado correctamente: ${user?.first_name}")
                    } catch (e: Exception) {
                        _accountData.value = null
                        Log.e("IncidentViewModel", "Error al obtener usuario con ID $userId: ${e.message}", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("IncidentViewModel", "Error al cargar incidente con ID $id: ${e.message}", e)
                _currentIncident.value = null
                _accountData.value = null
            }
        }
    }

    fun deleteIncident() {
        val incident = _currentIncident.value ?: return

        viewModelScope.launch {
            _isDeleting.value = true
            _deleteError.value = null
            
            try {
                val deletedIncident = deleteIncidentUseCase(incident._id!!)
                if (deletedIncident != null) {
                    _deleteSuccess.value = true
                    Log.d("IncidentViewModel", "Incidente eliminado correctamente")
                } else {
                    _deleteError.value = "No se pudo eliminar el incidente"
                    Log.e("IncidentViewModel", "deleteIncidentUseCase retornó null")
                }
            } catch (e: Exception) {
                _deleteError.value = "Error al eliminar: ${e.localizedMessage ?: e.message}"
                Log.e("IncidentViewModel", "Error al eliminar incidente: ${e.message}", e)
            } finally {
                _isDeleting.value = false
            }
        }
    }

    fun isOwner(): Boolean {
        val currentUser = getCurrentUserUseCase()?.uid
        val incidentUserId = _currentIncident.value?.user_id
        return currentUser != null && currentUser == incidentUserId
    }

    fun resetDeleteSuccess() {
        _deleteSuccess.value = false
    }

    fun resetDeleteError() {
        _deleteError.value = null
    }
}