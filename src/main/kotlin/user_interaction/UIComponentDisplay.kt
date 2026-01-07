package org.wiamotit1e.user_interaction

import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Alert
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.wiamotit1e.Sentence
import org.wiamotit1e.TranscriptSegment
import java.io.File
import java.util.concurrent.CompletableFuture
import kotlin.math.max

class UIComponentDisplay(
    val data: UIComponentData = UIComponentData(),
    val stage: Stage
) {
    
    val uiComponentAction = UIComponentAction(
        data = data,
        selectFile = { description, extensions ->
            val future = CompletableFuture<File?>()
            Platform.runLater {
                val result = FileChooser().apply {
                    title = description
                    extensionFilters.add(FileChooser.ExtensionFilter(description, extensions))
                }.showOpenDialog(stage)
                future.complete(result)
            }
            future.get() // 等待结果
        },
        saveFile = { description, extensions ->
            val future = CompletableFuture<File?>()
            Platform.runLater {
                val result = FileChooser().apply {
                    title = description
                    extensionFilters.add(FileChooser.ExtensionFilter(description, extensions))
                }.showSaveDialog(stage)
                future.complete(result)
            }
            future.get() // 等待结果
        }
    )
    
    val doForMessage = DoForMessage(
        onSuccess = {},
        onFailure = { message ->
            Platform.runLater {
                Alert(Alert.AlertType.ERROR).apply {
                    title = "错误"
                    headerText = "操作失败"
                    contentText = message.error
                    showAndWait()
                }
            }
        },
        onWarning = { message ->
            Platform.runLater {
                Alert(Alert.AlertType.WARNING).apply {
                    title = ""
                    headerText = ""
                    contentText = message.content
                    showAndWait()
                }
            }
        },
        onInfo = { message ->
            Platform.runLater {
                Alert(Alert.AlertType.INFORMATION).apply {
                    title = ""
                    headerText = ""
                    contentText = message.content
                    showAndWait()
                }
            }
        }
    )
    
    val aipKeyField: TextField = TextField().apply {
        prefWidth = 400.0
        textProperty().bindBidirectional(data.apiKey)
    }
    
    val apiKeyDisplayButton: Button = Button("配置").apply {
        text = "配置"
        setOnAction {
            uiComponentAction.onApiKeyDisplayButton()
        }
    }
    
    val apiKeySaveButton: Button = Button("保存").apply {
        text = "保存"
        setOnAction {
            uiComponentAction.onApiKeySaveButton()
        }
    }
    
    val filePathTextField: TextField = TextField().apply {
        prefWidth = 400.0
        isEditable = false
        promptText = "选择文件..."
        textProperty().bindBidirectional(data.filePath)
    }
    
    val selectFileButton: Button = ResultedButton("选择文件", onResult = doForMessage).apply {
        text = "选择文件"
        setResultedOnSuspendAction { uiComponentAction.onSelectFileButton() }
    }
    
    val updateFileButton: Button = ResultedButton(
        var1 = "上传文件",
        onResult = doForMessage
    ).apply {
        text = "上传文件"
        setResultedOnSuspendAction { uiComponentAction.onUpdateFileButton() }
    }
    
    val urlTextField: TextField = TextField().apply {
        prefWidth = 400.0
        text = ""
        textProperty().bindBidirectional(data.url)
    }
    
    val transcribeButton: Button = ResultedButton("转换", onResult = doForMessage).apply {
        text = "转换"
        setResultedOnSuspendAction { uiComponentAction.onTranscribeButton() }
    }
    
    val queryButton: Button = ResultedButton("查询转换", onResult = doForMessage).apply {
        text = "查询转换"
        setResultedOnSuspendAction { uiComponentAction.onQueryButton() }
    }
    
    val getResultButton: Button = ResultedButton("获取结果", onResult = doForMessage).apply {
        text = "获取结果"
        setResultedOnSuspendAction { uiComponentAction.onGetResultButton() }
    }
    
    val saveResultButton: Button = ResultedButton("保存结果", onResult = doForMessage).apply {
        text = "保存结果"
        setResultedOnSuspendAction { uiComponentAction.onSaveResultButton() }
    }
    
    val getResultFromFileButton: Button = ResultedButton("从文件获取结果", onResult = doForMessage).apply {
        text = "从文件获取结果"
        setResultedOnSuspendAction { uiComponentAction.onGetResultFromFileButton() }
    }
    
    val resultListView: ListView<String> = ListViewGenerator<String>(content = { it })
        .bindItems(data.results)
        .bindSelectedItem(data.selectedResult)
        .get()
        .apply {
            prefHeight = 200.0
            prefWidth = 600.0
        }
    
    val sentenceListView: ListView<Sentence> = ListViewGenerator<Sentence>(
        content = { sentence -> sentence.text },
        style = { sentenceListView, sentence, _ -> if (sentence.text.length >= 80) "-fx-text-fill: red;" else "" },
        onSelectionIndexChanged = { index ->
            transcriptSegmentTableView.apply {
                Platform.runLater {
                    scrollTo(data.sentences.take(max(0, data.selectedSentenceIndex.get())).sumOf { it.words.size })
                    refresh()
                }
            }
        })
        .bindItems(data.sentences)
        .bindSelectedIndex(data.selectedSentenceIndex)
        .get()
        .apply {
            prefHeight = 200.0
            prefWidth = 600.0
        }
    
    val transcriptSegmentTableView: TableView<TranscriptSegment> = TableViewGenerator<TranscriptSegment>(
        tableColumnConfigs = listOf(
            TableColumnConfig<TranscriptSegment, Any>(
                title = "文本",
                value = { it.text },
                content = { it as String},
                style = { column, _, index ->
                    if (data.selectedSentenceIndex.get() < 0) return@TableColumnConfig ""
                    val first: Int = data.sentences.take(data.selectedSentenceIndex.get()).sumOf { it.words.size }
                    val last: Int = data.sentences.take(data.selectedSentenceIndex.get() + 1).sumOf { it.words.size } - 1
                    if (index >= first && index <= last) return@TableColumnConfig "-fx-text-fill: red;"
                    ""
                }
            ),
            TableColumnConfig<TranscriptSegment, Any>(
                title = "置信度",
                value = { it.confidence },
                content = { it.toString() }
            ),
            TableColumnConfig<TranscriptSegment, Any>(
                title = "开始",
                value = { it.start },
                content = { it.toString() }
            ),
            TableColumnConfig<TranscriptSegment, Any>(
                title = "结束",
                value = { it.end },
                content = { it.toString() }
            )
        )
    )
        .bindItems(data.transcriptSegments)
        .bindSelectedIndex(data.selectedTranscriptSegmentIndex)
        .get().apply {
        prefHeight = 200.0
        prefWidth = 600.0
    }
    
    val mergeSentencesButton: Button = ResultedButton("合成句子", onResult = doForMessage).apply {
        text = "合成句子"
        setResultedOnAction {
            val result = uiComponentAction.onMergeSentencesButton()
            if (result is Message.Success) {
                sentenceListView.refresh()
                transcriptSegmentTableView.refresh()
            }
            result
        }
    }
    
    val splitSentenceButton: Button = ResultedButton("分割句子", onResult = doForMessage).apply {
        text = "分割句子"
        setResultedOnAction {
            val result = uiComponentAction.onSplitSentenceButton()
            if (result is Message.Success) {
                sentenceListView.refresh()
                transcriptSegmentTableView.refresh()
            }
            result
        }
    }
    
    val playSelectedSentenceButton: Button = ResultedButton("播放选中句子", onResult = doForMessage).apply {
        text = "播放选中句子"
        setResultedOnAction { uiComponentAction.onPlaySelectedSentenceButton() }
    }
    
    val saveAsSubtitleEventButton: Button = ResultedButton("保存为字幕事件", onResult = doForMessage).apply {
        text = "保存为字幕事件"
        setResultedOnSuspendAction { uiComponentAction.onSaveAsSubtitleEventButton() }
    }
    
    fun createApiKeyBox(): VBox {
        return VBox(
            Label("密钥:"),
            HBox(
                10.0,
                aipKeyField,
                apiKeyDisplayButton,
                apiKeySaveButton
            )
        ).apply {
            spacing = 10.0
            padding = Insets(10.0)
        }
    }
    
    fun createFileSelectionBox(): VBox {
        return VBox(
            Label("选择文件:"),
            HBox(
                10.0,
                filePathTextField,
                selectFileButton,
                updateFileButton
            )
        ).apply {
            spacing = 10.0
            padding = Insets(10.0)
        }
    }
    
    fun createTranscribeBox(): VBox {
        return VBox(
            Label("URL:"),
            HBox(
                10.0,
                urlTextField,
                transcribeButton
            )
        ).apply {
            spacing = 10.0
            padding = Insets(10.0)
        }
    }
    
    fun createSearchBox(): VBox {
        val sentenceAndTranscriptSegmentBox = VBox(10.0).apply {
            children.addAll(
                VBox(Label("句子列表:"), sentenceListView).apply { spacing = 10.0 },
                VBox(Label("转录片段表格:"), transcriptSegmentTableView).apply { spacing = 10.0 }
            )
        }
        
        val sentenceOperationButtonBox =
            HBox(10.0, mergeSentencesButton, splitSentenceButton, playSelectedSentenceButton)
        
        return VBox(
            Label("转换查询:"),
            HBox(10.0, queryButton, getResultButton, saveResultButton, getResultFromFileButton),
            resultListView,
            sentenceAndTranscriptSegmentBox,
            sentenceOperationButtonBox,
            HBox(10.0, saveAsSubtitleEventButton)
        ).apply {
            spacing = 10.0
            padding = Insets(10.0)
        }
    }
    
}


class App: Application() {
    
    
    override fun start(stage: Stage) {
        
        val uiComponentDisplay = UIComponentDisplay(
            stage = stage
        )
        
        val mainWindow = VBox(
            uiComponentDisplay.createApiKeyBox(),
            uiComponentDisplay.createFileSelectionBox(),
            uiComponentDisplay.createTranscribeBox(),
            uiComponentDisplay.createSearchBox()
        ).apply {
            padding = Insets(20.0)
        }
        
        stage.apply {
            title = "Wiam字幕制作器"
            scene = Scene(mainWindow, 800.0, 1000.0)
            show()
        }
    }
}
fun main() {
    Application.launch(App::class.java)
}