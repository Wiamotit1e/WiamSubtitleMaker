package org.wiamotit1e

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.nio.file.Path

class AssemblyAI服务(val 密钥: String) {
    
    val 客户端 = HttpClient(CIO) {
        install(Logging) {
            level = LogLevel.HEADERS
        }
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
        engine {
            requestTimeout = 60000
        }
    }
    
    fun 上传文件(文件: Path): String = runBlocking {
        val 回复 = 客户端.post("https://api.assemblyai.com/v2/upload") {
            headers {
                append(HttpHeaders.Authorization, 密钥.trim())
                append(HttpHeaders.ContentType, "application/octet-stream")
            }
            setBody(文件.toFile().readBytes())
        }.bodyAsText()
        Json.decodeFromString<JsonObject>(回复)["upload_url"]!!.jsonPrimitive.content
        
    }
    
    fun 使用通用模型转文字(url : String): String = runBlocking {
        val 回复 = 客户端.post("https://api.assemblyai.com/v2/transcript") {
            headers {
                append(HttpHeaders.Authorization, 密钥.trim())
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                buildJsonObject {
                    put("audio_url", JsonPrimitive(url))
                    put("speech_model", JsonPrimitive("universal"))
                }
            )
        }.bodyAsText()
        Json.decodeFromString<JsonObject>(回复)["id"]!!.jsonPrimitive.content
    }
    
    fun 使用最好模型转文字(url : String): String = runBlocking {
        val 回复 = 客户端.post("https://api.assemblyai.com/v2/transcript") {
            headers {
                append(HttpHeaders.Authorization, 密钥.trim())
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                buildJsonObject {
                    put("audio_url", JsonPrimitive(url))
                    put("speech_model", JsonPrimitive("best"))
                }
            )
        }.bodyAsText()
        Json.decodeFromString<JsonObject>(回复)["id"]!!.jsonPrimitive.content
    }
    suspend fun 获取结果(id: String): String {
        while (true) {
            val 回复 = 客户端.get("https://api.assemblyai.com/v2/transcript/$id") {
                headers { append(HttpHeaders.Authorization, 密钥.trim()) }
            }
            val 结果 = Json.decodeFromString<JsonObject>(回复.bodyAsText())
            val status = 结果["status"]!!.jsonPrimitive.content
            
            when (status) {
                "completed" -> return 结果["text"]!!.jsonPrimitive.content
                "failed" -> throw Exception("转文字失败: ${结果["error"]}")
                else -> {
                    println("当前状态: $status, 等待处理...")
                    delay(5000) // 等待5秒后重试
                }
            }
        }
    }
    
    suspend fun 在结果中获取句子列表(id: String): List<String> {
        val 回复 = 客户端.get("https://api.assemblyai.com/v2/transcript/$id/sentences") {
            headers { append(HttpHeaders.Authorization, 密钥.trim()) }
        }
        return Json.decodeFromString<JsonObject>(回复.bodyAsText())["sentences"]!!
            .jsonArray
            .map {
                it.jsonObject["text"]!!.jsonPrimitive.content
            }
    }
    
    suspend fun 在结果中获取句子作为字幕事件列表(id: String, 样式: String): List<字幕事件> {
        val 回复 = 客户端.get("https://api.assemblyai.com/v2/transcript/$id/sentences") {
            headers { append(HttpHeaders.Authorization, 密钥.trim()) }
        }
        return Json.decodeFromString<JsonObject>(回复.bodyAsText())["sentences"]!!
            .jsonArray
            .map {
                字幕事件(
                    文本 = it.jsonObject["text"]!!.jsonPrimitive.content,
                    开始时间 = it.jsonObject["start"]!!.jsonPrimitive.int.毫秒到时间(),
                    结束时间 = it.jsonObject["end"]!!.jsonPrimitive.int.毫秒到时间(),
                    样式 = 样式
                )
            }
    }
}