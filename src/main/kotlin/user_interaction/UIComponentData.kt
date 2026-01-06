package org.wiamotit1e.user_interaction

import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableIntegerValue
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import org.wiamotit1e.Sentence
import org.wiamotit1e.TranscriptSegment

class UIComponentData {

    val apiKey: SimpleStringProperty = SimpleStringProperty("")
    
    val filePath: SimpleStringProperty = SimpleStringProperty("")
    
    val url: SimpleStringProperty = SimpleStringProperty("")
    
    val results: ObservableList<String> = FXCollections.observableArrayList<String>()
    
    val selectedResult: ObjectProperty<String> = SimpleObjectProperty<String>("")
    
    val sentences: ObservableList<Sentence> = FXCollections.observableArrayList<Sentence>()
    
    val selectedSentenceIndex: IntegerProperty = SimpleIntegerProperty(-1)
    
    val transcriptSegments: ObservableList<TranscriptSegment> = FXCollections.observableArrayList<TranscriptSegment>()
    
    val selectedTranscriptSegmentIndex: IntegerProperty = SimpleIntegerProperty(-1)
}