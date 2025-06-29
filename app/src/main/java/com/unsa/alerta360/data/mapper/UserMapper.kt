package com.unsa.alerta360.data.mapper

import com.unsa.alerta360.data.model.UserDto
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