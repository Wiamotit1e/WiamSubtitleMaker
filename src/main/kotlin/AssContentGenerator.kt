package org.wiamotit1e

object AssContentGenerator {
    
    private val subtitleEventHeader = "[Events]\nFormat: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n"
    
    fun generate(list: List<SubtitleEvent>): String {
        return subtitleEventHeader + list.joinToString("\n") { it.format() }
    }
}