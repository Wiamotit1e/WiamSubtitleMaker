package org.wiamotit1e

import java.nio.file.Path
import java.nio.file.Files

fun 获取字幕事件(文件: Path): Iterator<字幕事件> {
    val lines = Files.readAllLines(文件)
    
    return lines.asSequence()
        .map { it.trim() }
        .filter { it.startsWith("Dialogue: ") }
        .map { line ->
            try {
                line.字幕事件()
            } catch (e: Exception) {
                // 忽略无法解析的行
                println("无法解析字幕行: $line, 错误: ${e.message}")
                null
            }
        }
        .filterNotNull()
        .iterator()
}