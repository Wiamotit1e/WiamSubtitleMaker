package org.wiamotit1e

import java.nio.file.Path

object 将字幕改为时间: 生成字幕 {
    override fun 开始(输入字幕事件: 字幕事件): 字幕事件 {
        
        return 输入字幕事件.copy(
            文本 = "${输入字幕事件.开始时间}-${输入字幕事件.结束时间}"
        )
    }
}