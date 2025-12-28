package org.wiamotit1e

data class 字幕事件(
    val 层级: Int = 0,
    val 开始时间: 时间,
    val 结束时间: 时间,
    val 样式: String,
    val 名称: String = "",
    val 左边距: Int = 0,
    val 右边距: Int = 0,
    val 垂直边距: Int = 0,
    val 效果: String = "",
    val 文本: String
) {
    override fun toString(): String {
        return "Dialogue: $层级,$开始时间,$结束时间,$样式,$名称,$左边距,$右边距,$垂直边距,$效果,$文本"
    }
}


fun String.字幕事件(): 字幕事件 {
    if (!this.startsWith("Dialogue: ")) {
        throw IllegalArgumentException("不是有效的Dialogue行: $this")
    }
    
    
    val 前缀 = "Dialogue: "
    val 内容部分 = this.substring(前缀.length)
    
    var 逗号位置 = mutableListOf<Int>()
    var 当前位置 = 0
    var 逗号计数 = 0
    
    while (当前位置 < 内容部分.length && 逗号计数 < 9) {
        if (内容部分[当前位置] == ',') {
            逗号位置.add(当前位置)
            逗号计数++
        }
        当前位置++
    }
    
    if (逗号位置.size < 9) {
        throw IllegalArgumentException("Dialogue行格式不正确: $this")
    }
    
    val 部分 = mutableListOf<String>()
    var 起始位置 = 0
    
    for (位置 in 逗号位置) {
        部分.add(内容部分.substring(起始位置, 位置))
        起始位置 = 位置 + 1
    }
    
    val 文本 = 内容部分.substring(起始位置).trim()
    
    return 字幕事件(
        层级 = 部分[0].trim().toInt(),
        开始时间 = 部分[1].trim().时间(),
        结束时间 = 部分[2].trim().时间(),
        样式 = 部分[3].trim(),
        名称 = 部分[4].trim(),
        左边距 = 部分[5].trim().toInt(),
        右边距 = 部分[6].trim().toInt(),
        垂直边距 = 部分[7].trim().toInt(),
        效果 = 部分[8].trim(),
        文本 = 文本
    )
}

fun main() {
    println("Dialogue: 0,0:05:35.18,0:05:36.91,ENSub1,,0,0,0,,".字幕事件())
}