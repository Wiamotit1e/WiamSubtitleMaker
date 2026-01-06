package org.wiamotit1e

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File


@Serializable
data class Config(val apiKey: String) {
    companion object{
        val default = Config("")
    }
}

fun getConfig(): Config {
    val configFile = File("config.json")
    if (!configFile.exists()) {
        configFile.writeText(Json.encodeToString(Config.default))
    }
    return Json.decodeFromString<Config>(configFile.readText())
}

fun saveConfig(config: Config) {
    val configFile = File("config.json")
    configFile.writeText(Json.encodeToString(config))
}