import org.junit.jupiter.api.Test
import org.wiamotit1e.Sentence
import org.wiamotit1e.toSentence
import org.wiamotit1e.TranscriptSegment
import org.wiamotit1e.WTime
import org.wiamotit1e.merge
import org.wiamotit1e.split
import org.wiamotit1e.AssContentGenerator
import org.wiamotit1e.SubtitleEvent
import kotlin.test.assertEquals

class Test {
    @Test
    fun wordsToSentences() {
        val word1 = TranscriptSegment(
            confidence = 0.0,
            start = 0,
            end = 0,
            text = "a",
            speaker = null
        )
        val word2 = TranscriptSegment(
            confidence = 0.0,
            start = 0,
            end = 0,
            text = "b",
            speaker = null
        )
        val word3 = TranscriptSegment(
            confidence = 0.0,
            start = 0,
            end = 0,
            text = "c",
            speaker = null
        )
        assertEquals(
            expected = listOf(word1, word2, word3).toSentence(),
            actual = Sentence(text = "a b c", words = listOf(word1, word2, word3))
        )
        assertEquals(
            expected = listOf(word1).toSentence(),
            actual = Sentence(text = "a", words = listOf(word1))
        )
    }
    
    @Test
    fun mergeSentences() {
        val sentence1 = Sentence("Hello world!", listOf(
            TranscriptSegment(0.9, 1000, 2000, "Hello", null),
            TranscriptSegment(0.8, 2000, 3000, "world!", null),
        ))
        val sentence2 = Sentence("I'm gay!", listOf(
            TranscriptSegment(0.8, 3000, 4000, "I'm", null),
            TranscriptSegment(0.7, 4000, 5000, "gay!", null)
        ))
        
        sentence1.merge(sentence2)
        assertEquals("Hello world! I'm gay!", sentence1.text)
        assertEquals(
            listOf<TranscriptSegment>(
            TranscriptSegment(0.9, 1000, 2000, "Hello", null),
            TranscriptSegment(0.8, 2000, 3000, "world!", null),
            TranscriptSegment(0.8, 3000, 4000, "I'm", null),
            TranscriptSegment(0.7, 4000, 5000, "gay!", null)),
            sentence1.words
        )
    }
    
    @Test
    fun splitSentence() {
        val segments = listOf(
            TranscriptSegment(0.9, 1000, 2000, "Hello", null),
            TranscriptSegment(0.8, 2000, 3000, "world!", null),
            TranscriptSegment(0.7, 3000, 4000, "I'm", null),
            TranscriptSegment(0.6, 4000, 5000, "your", null),
            TranscriptSegment(0.5, 5000, 6000, "dad!", null)
        )
        val sentence = segments.toSentence()
        val (first, second) = sentence.split(1)
        assertEquals("Hello", first.text)
        assertEquals(listOf(segments[0]), first.words)
        assertEquals("world! I'm your dad!", second.text)
        assertEquals(listOf(segments[1], segments[2], segments[3], segments[4]), second.words)
        
        
        val (third, fourth) = sentence.split(2)
        assertEquals("Hello world!", third.text)
        assertEquals(listOf(segments[0], segments[1]), third.words)
        assertEquals("I'm your dad!", fourth.text)
        assertEquals(listOf(segments[2], segments[3], segments[4]), fourth.words)
    }

    @Test
    fun subtitleEventFormat() {
        val wtime = WTime(1, 23, 45, 67)
        val subtitleEvent = SubtitleEvent(
            layer = 1,
            start = wtime,
            end = wtime,
            style = "Default",
            name = "TestName",
            marginL = 10,
            marginR = 20,
            marginV = 30,
            effect = "TestEffect",
            text = "Test Text"
        )

        val expected = "Dialogue: 1,1:23:45.67,1:23:45.67,Default,TestName,10,20,30,TestEffect,Test Text"
        assertEquals(expected, subtitleEvent.format())
    }

    @Test
    fun subtitleEventWithDefaults() {
        val wTime = WTime(0, 0, 10, 0)
        val subtitleEvent = SubtitleEvent(
            layer = 0,
            start = wTime,
            end = wTime,
            style = "Default",
            name = "",
            marginL = 0,
            marginR = 0,
            marginV = 0,
            effect = "",
            text = "Default Values Test"
        )

        val actual = subtitleEvent.format()
        
        assertEquals(actual, "Dialogue: 0,0:00:10.00,0:00:10.00,Default,,0,0,0,,Default Values Test")
    }

    @Test
    fun assContentGeneratorEmptyList() {
        val result = AssContentGenerator.generate(emptyList())
        val expected = "[Events]\nFormat: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text\n"
        assertEquals(expected, result)
    }

    @Test
    fun assContentGeneratorWithEvents() {
        val wTime = WTime(0, 0, 1, 0)
        val wTime2 = WTime(0, 0, 2, 0)
        val events = listOf(
            SubtitleEvent(
                layer = 0,
                start = wTime,
                end = wTime2,
                style = "Default",
                text = "Hello World"
            ),
            SubtitleEvent(
                layer = 1,
                start = wTime2,
                end = WTime(0, 0, 3, 0),
                style = "Bold",
                name = "Speaker1",
                text = "Second subtitle"
            )
        )

        val result = AssContentGenerator.generate(events)
        
        val expected = """
            [Events]
            Format: Layer, Start, End, Style, Name, MarginL, MarginR, MarginV, Effect, Text
            Dialogue: 0,0:00:01.00,0:00:02.00,Default,,0,0,0,,Hello World
            Dialogue: 1,0:00:02.00,0:00:03.00,Bold,Speaker1,0,0,0,,Second subtitle
        """.trimIndent()
        
        assertEquals(expected, result)
    }
}