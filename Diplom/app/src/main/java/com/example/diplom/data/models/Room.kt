package com.example.diplom.models

data class Room(
    val id: Int,
    val name: String,
    val temperatureSensorId: Int?, // ID датчика температуры (null если не назначен)
    val humiditySensorId: Int?,    // ID датчика влажности (null если не назначен)
    var lightOn: Boolean,
    val socketIds: List<Int>       // Список ID розеток в этой комнате
)

data class TemperatureSensor(
    val id: Int,
    val name: String,
    val temperature: Float
)

data class HumiditySensor(
    val id: Int,
    val name: String,
    val humidity: Float
)

data class Socket(
    val id: Int,
    val name: String,               // Название розетки (например, "Розетка #1")
    var deviceName: String,         // Название подключенного устройства (редактируемое)
    var isOn: Boolean
)

data class RoomAutoOffSettings(
    val roomId: Int,
    var enabled: Boolean = false,
    var turnOffLights: Boolean = true,
    var turnOffSockets: List<Int> = emptyList() // ID розеток которые нужно выключать
)