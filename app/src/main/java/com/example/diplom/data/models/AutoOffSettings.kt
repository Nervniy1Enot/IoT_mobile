package com.example.diplom.models

data class RoomAutoOffSettings(
    val roomId: Int,
    var enabled: Boolean = true,
    var turnOffLights: Boolean = true,
    var turnOffSockets: MutableSet<Int> = mutableSetOf() // ID розеток для выключения
)

data class AutoOffSettings(
    var enabled: Boolean = false,
    var delayMinutes: Int = 15,
    var roomSettings: MutableMap<Int, RoomAutoOffSettings> = mutableMapOf()
) {
    override fun toString(): String {
        return "AutoOffSettings(enabled=$enabled, delayMinutes=$delayMinutes, roomSettings=$roomSettings)"
    }
}