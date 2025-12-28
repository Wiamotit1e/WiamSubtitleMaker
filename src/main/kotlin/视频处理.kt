package org.wiamotit1e

import java.nio.file.Path


fun 按时间截取视频(视频路径: Path, 输出目录: Path, 开始时间: 时间, 停止时间: 时间) {
    val 命令 = arrayOf(
        "ffmpeg",
        "-i", "\"$视频路径\"",  // 将输入参数移到前面
        "-ss", 开始时间.toString(),  // 在解码时指定开始时间
        "-to", 停止时间.toString(),  // 在解码时指定结束时间
        "-c", "copy",
        "-map", "0",
        "-y",
        "\"${输出目录.resolve("${视频路径.fileName}-${开始时间.toString().replace(":", "-")}-${停止时间.toString().replace(":", "-")}.${视频路径.toString().substringAfterLast('.', "")}").toString()}\""
    )
    println(命令.joinToString(" "))
    ProcessBuilder(*命令)
        .redirectOutput(ProcessBuilder.Redirect.INHERIT)
        .redirectError(ProcessBuilder.Redirect.INHERIT)
        .start()
        .waitFor()
}