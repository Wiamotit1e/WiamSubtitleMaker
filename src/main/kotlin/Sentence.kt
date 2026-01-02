package org.wiamotit1e

import kotlinx.serialization.Serializable


@Serializable
data class Sentence(
    var text: String,
    var words: List<TranscriptSegment>
)

fun Sentence.subtitleEvent(style: String = "Default") = SubtitleEvent(
    style = style,
    text = text,
    start = words[0].start.millisecondsToTime(),
    end = words.last().end.millisecondsToTime()
)