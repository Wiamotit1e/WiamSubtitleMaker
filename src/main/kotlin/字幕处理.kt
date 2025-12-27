package org.wiamotit1e

import java.nio.file.Path
import java.nio.file.Files

fun 字幕文件处理(文件: Path): List<字幕事件> {
    val lines = Files.readAllLines(文件)
    val 字幕事件列表 = mutableListOf<字幕事件>()
    
    for (line in lines) {
        val trimmedLine = line.trim()
        
        // 只处理以Dialogue: 开头的行
        if (trimmedLine.startsWith("Dialogue: ")) {
            try {
                val event = trimmedLine.字幕事件()
                字幕事件列表.add(event)
            } catch (e: Exception) {
                // 忽略无法解析的行
                println("无法解析字幕行: $trimmedLine, 错误: ${e.message}")
            }
        }
    }
    
    return 字幕事件列表
}