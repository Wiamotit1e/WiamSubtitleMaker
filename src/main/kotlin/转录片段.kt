package org.wiamotit1e

import kotlinx.serialization.Serializable


@Serializable
data class 转录片段(
    val confidence: Double,
    val start: Int,
    val end: Int,
    val text: String,
    val speaker: String? = null
)
