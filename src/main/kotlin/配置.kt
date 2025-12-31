package org.wiamotit1e

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


@Serializable
data class 配置(val 密钥: String) {
    companion object{
        val 默认 = 配置("")
    }
}

fun 获取配置(): 配置 {
    val 配置文件 = File("config.json")
    if (!配置文件.exists()) {
        配置文件.writeText(Json.encodeToString(配置.默认))
    }
    return Json.decodeFromString<配置>(配置文件.readText())
}

fun 保存配置(配置: 配置) {
    val 配置文件 = File("config.json")
    配置文件.writeText(Json.encodeToString(配置))
}