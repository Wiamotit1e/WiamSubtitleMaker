package org.wiamotit1e

import kotlinx.serialization.Serializable


@Serializable
data class Sentence(
    var text: String,
    var words: List<TranscriptSegment>
)

fun Sentence.toSubtitleEvent(style: String = "Default") = SubtitleEvent(
    style = style,
    text = text,
    start = words[0].start.millisecondsToTime(),
    end = words.last().end.millisecondsToTime()
)

fun Sentence.merge(var1: Sentence) {
    if (var1.words.isEmpty()) return
    
    if (this.text.isNotEmpty()) {
        this.text += " " + var1.text
    } else {
        this.text += var1.text
    }
    this.words += var1.words
}

fun Sentence.split(index: Int): Pair<Sentence, Sentence> {
    require(index in 1 until this.words.size) { "Cannot split sentence at index $index" }
    val var1 = this.words.subList(0, index)
    val var2 = this.words.subList(index, this.words.size)
    return Pair(var1.toSentence(), var2.toSentence())
}
fun List<TranscriptSegment>.toSentence(): Sentence {
    return Sentence(
        text = this.joinToString(" ") { it.text },
        words = this
    )
}