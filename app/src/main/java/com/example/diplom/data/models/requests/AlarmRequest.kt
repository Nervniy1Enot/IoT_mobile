package com.example.diplom.data.models.requests

data class AlarmRequest(
    val enabled: Boolean,
    val time: String? = null
)