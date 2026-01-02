package org.wiamotit1e

data class SubtitleEvent(
    val layer: Int = 0,
    val start: WTime,
    val end: WTime,
    val style: String,
    val name: String = "",
    val marginL: Int = 0,
    val marginR: Int = 0,
    val marginV: Int = 0,
    val effect: String = "",
    val text: String
) {
    override fun toString(): String {
        return "Dialogue: $layer,$start,$end,$style,$name,$marginL,$marginR,$marginV,$effect,$text"
    }
}