package org.wiamotit1e

import kotlinx.serialization.Serializable


@Serializable
data class TranscriptSegment(
    val confidence: Double,
    val start: Int,
    val end: Int,
    val text: String,
    val speaker: String? = null
)
