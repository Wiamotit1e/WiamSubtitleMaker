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