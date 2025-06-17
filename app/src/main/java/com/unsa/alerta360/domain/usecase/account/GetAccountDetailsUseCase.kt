package com.unsa.alerta360.domain.usecase.account

import com.unsa.alerta360.domain.model.Account
import com.unsa.alerta360.domain.model.Result
import com.unsa.alerta360.domain.repository.AccountRepository
import javax.inject.Inject

class GetAccountDetailsUseCase @Inject constructor(
    private val accountRepository: AccountRepository
) {
    suspend operator fun invoke(userId: String): Result<Account?> {
        return try {
            val account = accountRepository.getAccount(userId)
            Result.Success(account)
        } catch (e: Exception) {
            Result.Error(
                exception = e,
                message = "Error al obtener detalles de la cuenta: ${e.message}"
            )
        }
    }
}