package org.wiamotit1e

data class WTime(val hour: Int, val minute: Int, val second: Int, val centisecond: Int) {
    override fun toString(): String {
        return String.format("%d:%02d:%02d.%02d", hour, minute, second, centisecond)
    }
}

fun Int.millisecondsToTime(): WTime {
    val totalMilliseconds = this
    val hour = totalMilliseconds / 1000 / 60 / 60
    val minute = (totalMilliseconds / 1000 / 60 % 60).toInt()
    val second = (totalMilliseconds / 1000 % 60).toInt()
    val millisecond = (totalMilliseconds % 1000).toInt()
    val centisecond = millisecond / 10
    return WTime(hour, minute, second, centisecond)
}