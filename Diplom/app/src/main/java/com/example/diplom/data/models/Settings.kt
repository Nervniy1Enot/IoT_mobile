package com.example.diplom.data.models

data class Settings(
    val alarm: AlarmSettings,
    val auto_lights: AutoLightsSettings,
    val notifications: NotificationSettings
)

data class AlarmSettings(
    val enabled: Boolean,
    val time: String
)

data class AutoLightsSettings(
    val enabled: Boolean
)

data class NotificationSettings(
    val temperature_alert: Boolean,
    val humidity_alert: Boolean,
    val user_arrival: Boolean
)