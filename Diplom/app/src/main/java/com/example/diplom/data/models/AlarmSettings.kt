package com.example.diplom.models

data class AlarmSettings(
    var enabled: Boolean = false,
    var hour: Int = 7,
    var minute: Int = 0,
    var activeDays: BooleanArray = BooleanArray(7) { false } // Пн-Вс
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AlarmSettings

        if (enabled != other.enabled) return false
        if (hour != other.hour) return false
        if (minute != other.minute) return false
        if (!activeDays.contentEquals(other.activeDays)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + hour
        result = 31 * result + minute
        result = 31 * result + activeDays.contentHashCode()
        return result
    }
}

data class AutoOffSettings(
    var enabled: Boolean = false,
    var delayMinutes: Int = 5,
    val roomSettings: MutableList<RoomAutoOffSettings> = mutableListOf()
)