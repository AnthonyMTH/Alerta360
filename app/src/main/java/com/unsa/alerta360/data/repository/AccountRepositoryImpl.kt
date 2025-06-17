package com.unsa.alerta360.data.repository

import com.unsa.alerta360.data.mapper.toDomain
import com.unsa.alerta360.data.mapper.toDto
import com.unsa.alerta360.data.network.AccountApiService
import com.unsa.alerta360.domain.model.Account
import com.unsa.alerta360.domain.repository.AccountRepository
import javax.inject.Inject

class AccountRepositoryImpl @Inject constructor(
    private val accountApiService: AccountApiService
) : AccountRepository {

    override suspend fun getAccount(userId: String): Account {
        val dto = accountApiService.getAccountById(userId)
        return dto.toDomain() // Convertir de AccountDTO a Account
    }

    override suspend fun updateAccount(userId: String, account: Account) {
        val dto = account.toDto() // Convertir de Account a AccountDTO
        accountApiService.updateAccount(userId, dto)
    }
}

