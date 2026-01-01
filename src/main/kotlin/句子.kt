package org.wiamotit1e

import kotlinx.serialization.Serializable


@Serializable
data class 句子(
    var text: String,
    var words: List<转录片段>
)

fun 句子.字幕事件(样式: String = "Default") = 字幕事件(
    样式 = 样式,
    文本 = text,
    开始时间 = words[0].start.毫秒到时间(),
    结束时间 = words.last().end.毫秒到时间()
)