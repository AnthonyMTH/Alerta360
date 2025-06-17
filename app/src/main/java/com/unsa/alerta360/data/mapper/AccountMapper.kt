package com.unsa.alerta360.data.mapper

import com.unsa.alerta360.data.model.AccountDTO
import com.unsa.alerta360.domain.model.Account

fun AccountDTO.toDomain(): Account = Account(
    _id = _id,
    first_name = first_name,
    last_name = last_name,
    email = email,
    phone_number = phone_number,
    district = district,
    dni = dni,
    createdAt = createdAt,
    updatedAt = updatedAt,
    __v = __v
)

fun Account.toDto(): AccountDTO = AccountDTO(
    _id = _id,
    first_name = first_name,
    last_name = last_name,
    email = email,
    phone_number = phone_number,
    district = district,
    dni = dni,
    createdAt = createdAt,
    updatedAt = updatedAt,
    __v = __v
)
