package com.example.diplom.models

import java.util.*

data class HistoryEvent(
    val id: Int,
    val timestamp: Date,
    val type: EventType,
    val title: String,
    val description: String,
    val roomName: String? = null,
    val deviceName: String? = null,
    val userName: String? = null
)

enum class EventType(val displayName: String, val icon: String) {
    RFID_ENTRY("RFID вход", "🚪"),
    RFID_EXIT("RFID выход", "🚶"),
    LIGHT_ON("Включение света", "💡"),
    LIGHT_OFF("Выключение света", "🌙"),
    SOCKET_ON("Включение розетки", "🔌"),
    SOCKET_OFF("Выключение розетки", "⚡"),
    ALARM_TRIGGERED("Сработал будильник", "⏰"),
    AUTO_OFF_TRIGGERED("Автовыключение", "🏠"),
    SYSTEM_EVENT("Системное событие", "⚙️")
}