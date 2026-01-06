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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.nio.file.Path

class AssemblyAIService(var apiKey: String) {
    
    private val client = HttpClient(CIO) {
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
    
    suspend fun updateFile(filePath: Path): String {
        val response = client.post("https://api.assemblyai.com/v2/upload") {
            headers {
                append(HttpHeaders.Authorization, apiKey.trim())
                append(HttpHeaders.ContentType, "application/octet-stream")
            }
            setBody(filePath.toFile().readBytes())
        }.bodyAsText()
        return Json.decodeFromString<JsonObject>(response)["upload_url"]!!.jsonPrimitive.content
    }
    
    suspend fun transcribeWithGeneralModel(url: String): String {
        val response = client.post("https://api.assemblyai.com/v2/transcript") {
            headers {
                append(HttpHeaders.Authorization, apiKey.trim())
                append(HttpHeaders.ContentType, "application/json")
            }
            setBody(
                buildJsonObject {
                    put("audio_url", JsonPrimitive(url))
                    put("speech_model", JsonPrimitive("universal"))
                }
            )
        }.bodyAsText()
        return Json.decodeFromString<JsonObject>(response)["id"]!!.jsonPrimitive.content
    }
    
    suspend fun queryTranscription(): List<String> {
        val response = client.get("https://api.assemblyai.com/v2/transcript") {
            headers {
                append(HttpHeaders.Authorization, apiKey.trim())
            }
        }.bodyAsText()
        return Json.decodeFromString<JsonObject>(response)["transcripts"]!!.jsonArray.map { 
            it.jsonObject["id"]!!.jsonPrimitive.content
        }
    }
    
    suspend fun getSentencesFromResult(id: String): List<Sentence> {
        val response = client.get("https://api.assemblyai.com/v2/transcript/$id/sentences") {
            headers { append(HttpHeaders.Authorization, apiKey.trim()) }
        }.bodyAsText()
        return Json.decodeFromString<JsonObject>(response)["sentences"]!!
            .jsonArray
            .map {
                Sentence(
                    it.jsonObject["text"]!!.jsonPrimitive.content,
                    it.jsonObject["words"]!!.jsonArray.map {
                        Json.decodeFromString<TranscriptSegment>(it.jsonObject.toString())
                    }
                )
            }
    }
}


suspend fun main() {
    val config = getConfig()
    val service = AssemblyAIService(config.apiKey)
    val id = service.queryTranscription()[1]
    service.getSentencesFromResult(id).forEach { println(it) }
}