package org.wiamotit1e

import java.nio.file.Path


fun 按时间截取视频(视频路径: Path, 输出目录: Path, 开始时间: 时间, 停止时间: 时间) {
    val 命令 = arrayOf(
        "ffmpeg",
        "-ss", 开始时间.toString(),
        "-i", 视频路径.toString(),
        "-to", 停止时间.toString(),
        "-c", "copy",
        "-map", "0",
        "-y",
        输出目录.resolve("${视频路径.fileName}-${开始时间}-${停止时间}" +
                视频路径.toString().substringAfterLast('.', "")
        ).toString()
    )
    println(命令.joinToString(" "))
    ProcessBuilder(*命令).start().waitFor()
}

