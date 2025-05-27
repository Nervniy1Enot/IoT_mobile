package com.example.diplom.data.models

data class Status(
    val user_home: Boolean,
    val active_user: String?,
    val last_seen_rfid: String,
    val last_seen_time: String
)