package org.wiamotit1e

import java.nio.file.Path

interface 生成字幕 {
    fun 开始(输入字幕事件: 字幕事件): 字幕事件
}