import org.junit.jupiter.api.Test
import org.wiamotit1e.Sentence
import org.wiamotit1e.toSentence
import org.wiamotit1e.TranscriptSegment
import org.wiamotit1e.merge
import org.wiamotit1e.split
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
}