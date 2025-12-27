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

fun main() {
    按时间截取视频(
        Path.of("D:\\Wiam\\WiamVideoMaker\\input\\Sibling Stories! WE ROBBED A LIBRARY AND GOT AWAY WITH IT [RfipDJoovM4].mp4"),
        Path.of("D:\\Wiam\\WiamVideoMaker\\output\\"),
        "0:00:04.90".时间(),
        "0:00:06.60".时间()
    )
}