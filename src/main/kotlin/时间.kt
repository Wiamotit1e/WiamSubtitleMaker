package org.wiamotit1e

data class 时间(val 时: Int, val 分: Int, val 秒: Int, val 小数秒: Int) {
    override fun toString(): String {
        return String.format("%02d:%02d:%02d.%02d", 时, 分, 秒, 小数秒)
    }
}

fun String.时间(): 时间 {
    val 数组 = this.split(":")
    val 时 = 数组[0].toInt()
    val 分 = 数组[1].toInt()
    val 秒 = 数组[2].split(".")[0].toInt()
    val 小数秒 = 数组[2].split(".")[1].toInt()
    return 时间(时, 分, 秒, 小数秒)
}

fun main() {
    println("1:02:03.04".时间())
}