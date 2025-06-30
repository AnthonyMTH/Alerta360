package com.unsa.alerta360.data.mapper

import com.unsa.alerta360.data.model.AccountDTO
import com.unsa.alerta360.data.model.UserDto
import com.unsa.alerta360.domain.model.Account
import com.unsa.alerta360.domain.model.User

fun UserDto.toDomain(): User = User(
    _id = uid,
    first_name = firstName,
    last_name = lastName,
    email = email,
    phone_number = phoneNumber,
    district = district,
    dni = dni,
    createdAt = createdAt,
    updatedAt = updatedAt,
    __v = version,
    password = password
)

fun User.toDto(): UserDto = UserDto(
    uid = _id,
    firstName = first_name,
    lastName = last_name,
    email = email,
    phoneNumber = phone_number,
    district = district,
    dni = dni,
    createdAt = createdAt,
    updatedAt = updatedAt,
    version = __v,
    password = password
)