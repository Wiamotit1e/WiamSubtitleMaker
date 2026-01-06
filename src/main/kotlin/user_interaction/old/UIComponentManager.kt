package org.wiamotit1e.user_interaction.old

import javafx.geometry.Insets
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.ListView
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import org.wiamotit1e.Sentence
import org.wiamotit1e.TranscriptSegment

/**
 * UI组件管理器 - 管理所有UI组件的创建和初始化
 */
class UIComponentManager {
    
    val textField: TextField = TextField().apply {
        prefWidth = 400.0
    }
    
    val apiKeyDisplayButton: Button = Button("配置").apply {
        text = "配置"
    }
    
    val aipKeySaveButton: Button = Button("保存").apply {
        text = "保存"
    }
    
    val filePathTextField: TextField = TextField().apply {
        prefWidth = 400.0
        isEditable = false
        promptText = "选择文件..."
    }
    
    val selectFileButton: Button = Button("选择文件").apply {
        text = "选择文件"
    }
    
    val updateFileButton: Button = Button("上传文件").apply {
        text = "上传文件"
    }
    
    val urlTextField: TextField = TextField().apply {
        prefWidth = 400.0
        text = ""
    }
    
    val transcribeButton: Button = Button("转换").apply {
        text = "转换"
    }
    
    val queryButton: Button = Button("查询转换").apply {
        text = "查询转换"
    }
    
    val getResultButton: Button = Button("获取结果").apply {
        text = "获取结果"
    }
    
    val saveResultButton: Button = Button("保存结果").apply {
        text = "保存结果"
    }
    
    val getResultFromFileButton: Button = Button("从文件获取结果").apply {
        text = "从文件获取结果"
    }
    
    val resultListView: ListView<String> = ListView<String>().apply {
        prefHeight = 150.0
        prefWidth = 600.0
    }
    
    val sentenceListView: ListView<Sentence> = ListView<Sentence>().apply {
        prefHeight = 200.0
        prefWidth = 600.0
    }
    
    val transcriptSegmentTableView: TableView<TranscriptSegment> = TableView<TranscriptSegment>().apply {
        prefHeight = 200.0
        prefWidth = 600.0
    }
    
    val mergeSentencesButton: Button = Button("合成句子").apply {
        text = "合成句子"
    }
    
    val splitSentenceButton: Button = Button("分割句子").apply {
        text = "分割句子"
    }
    
    val playSelectedSentenceButton: Button = Button("播放选中句子").apply {
        text = "播放选中句子"
    }
    
    val saveAsSubtitleEventButton: Button = Button("保存为字幕事件").apply {
        text = "保存为字幕事件"
    }
    
    fun createApiKeyBox(): VBox {
        return VBox(
            Label("密钥:"),
            HBox(
                10.0,
                textField,
                apiKeyDisplayButton,
                aipKeySaveButton
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