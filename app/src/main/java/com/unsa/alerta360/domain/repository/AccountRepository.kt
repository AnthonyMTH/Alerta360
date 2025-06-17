package com.unsa.alerta360.domain.repository

import com.unsa.alerta360.data.local.UserData
import com.unsa.alerta360.domain.model.Account

interface AccountRepository {
    suspend fun getAccount(userId: String): Account
    suspend fun updateAccount(userId: String, userData: Account)
}
