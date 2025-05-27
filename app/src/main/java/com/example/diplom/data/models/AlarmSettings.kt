package com.example.diplom.models

data class AlarmSettings(
    var enabled: Boolean = false,
    var hour: Int = 7,
    var minute: Int = 0,
    var days: BooleanArray = BooleanArray(7) { false }
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AlarmSettings

        if (enabled != other.enabled) return false
        if (hour != other.hour) return false
        if (minute != other.minute) return false
        if (!days.contentEquals(other.days)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = enabled.hashCode()
        result = 31 * result + hour
        result = 31 * result + minute
        result = 31 * result + days.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "AlarmSettings(enabled=$enabled, hour=$hour, minute=$minute, days=${days.contentToString()})"
    }
}