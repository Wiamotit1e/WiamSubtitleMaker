package org.wiamotit1e

data class 时间(val 时: Int, val 分: Int, val 秒: Int, val 小数秒: Int) {
    override fun toString(): String {
        return String.format("%d:%02d:%02d.%02d", 时, 分, 秒, 小数秒)
    }
}

fun Int.毫秒到时间(): 时间 {
    val 总毫秒 = this
    val 时 = 总毫秒 / 1000 / 60 / 60
    val 分 = (总毫秒 / 1000 / 60 % 60).toInt()
    val 秒 = (总毫秒 / 1000 % 60).toInt()
    val 毫秒 = (总毫秒 % 1000).toInt()
    val 小数秒 = 毫秒 / 10
    return 时间(时, 分, 秒, 小数秒)
}