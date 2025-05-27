package com.example.diplom.data.models

data class HomeStatus(
    val devices: Devices,
    val sensors: Sensors,
    val users: List<User>,
    val status: Status,
    val settings: Settings,
    val events: List<Event>
)