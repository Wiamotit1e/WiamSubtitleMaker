package org.wiamotit1e.user_interaction.old

import javafx.collections.FXCollections
import javafx.scene.control.Alert
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.PropertyValueFactory
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.util.Callback
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.wiamotit1e.AssemblyAIService
import org.wiamotit1e.Config
import org.wiamotit1e.Player
import org.wiamotit1e.Sentence
import org.wiamotit1e.TranscriptSegment
import org.wiamotit1e.getConfig
import org.wiamotit1e.saveConfig
import org.wiamotit1e.toSubtitleEvent
import org.wiamotit1e.user_interaction.old.ListViewMaker
import org.wiamotit1e.user_interaction.old.UIComponentManager
import java.nio.file.Path
import kotlin.io.path.exists

/**
 * 业务逻辑处理器 - 处理所有业务逻辑，与UI组件解耦
 */
class BusinessLogicProcessor(
    private val uiComponentManager: UIComponentManager,
    private val currentStage: Stage
) {
    private var config: Config = getConfig()
    private val sentences = mutableListOf<Sentence>()
    
    private val sentencesMaker  = ListViewMaker<Sentence>(
        listView = uiComponentManager.sentenceListView,
        content = { it.text },
        style = { if (it.text.length > 80) "-fx-text-fill: red;" else "" },
        onSelectionChanged = {
            if (it == null) return@ListViewMaker
            val sentenceIndex = uiComponentManager.sentenceListView.items.indexOf(it)
            if (sentenceIndex < 0 || sentenceIndex >= sentences.size) return@ListViewMaker
            val transcriptSegmentsOfSentence = sentences[sentenceIndex]
            if (transcriptSegmentsOfSentence.words.isEmpty()) return@ListViewMaker
            val firstTranscriptSegment = transcriptSegmentsOfSentence.words[0]
            val transcriptSegmentIndex =
                uiComponentManager.transcriptSegmentTableView.items.indexOf(firstTranscriptSegment)
            if (transcriptSegmentIndex < 0) return@ListViewMaker
            uiComponentManager.transcriptSegmentTableView.selectionModel.select(transcriptSegmentIndex)
            uiComponentManager.transcriptSegmentTableView.scrollTo(transcriptSegmentIndex)
            uiComponentManager.transcriptSegmentTableView.refresh()
        })
    
    fun initializeEventManager() {
        // 配置相关事件
        uiComponentManager.apiKeyDisplayButton.setOnAction {
            showApiKey()
        }
        
        uiComponentManager.aipKeySaveButton.setOnAction {
            saveApiKey()
        }
        
        // 文件选择相关事件
        uiComponentManager.selectFileButton.setOnAction {
            selectFile()
        }
        
        uiComponentManager.updateFileButton.setOnAction {
            updateFile()
        }
        
        // 转换相关事件
        uiComponentManager.transcribeButton.setOnAction {
            transcribeAudio()
        }
        
        // 查询相关事件
        uiComponentManager.queryButton.setOnAction {
            queryTranscription()
        }
        
        uiComponentManager.getResultButton.setOnAction {
            getResult()
        }
        
        uiComponentManager.saveResultButton.setOnAction {
            saveResult()
        }
        
        uiComponentManager.getResultFromFileButton.setOnAction {
            getResultFromFile()
        }
        
        // 句子操作相关事件
        uiComponentManager.mergeSentencesButton.setOnAction {
            mergeSentences()
        }
        
        uiComponentManager.splitSentenceButton.setOnAction {
            splitSentence()
        }
        
        uiComponentManager.playSelectedSentenceButton.setOnAction {
            playSelectedSentence()
        }
        
        uiComponentManager.saveAsSubtitleEventButton.setOnAction {
            saveAsSubtitleEvent()
        }
        
        
        initializeTranscriptSegmentTable()
    }

    private fun showApiKey() {
        uiComponentManager.textField.text = config.apiKey
    }

    private fun saveApiKey() {
        config = config.copy(apiKey = uiComponentManager.textField.text)
        saveConfig(config)
    }

    private fun selectFile() {
        val fileChooser = FileChooser().apply {
            title = "选择文件"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("所有文件", "*.*"),
            )
        }
        val selectedFile = fileChooser.showOpenDialog(currentStage)
        if (selectedFile != null) {
            uiComponentManager.filePathTextField.text = selectedFile.absolutePath
        }
    }

    private fun updateFile() {
        val filePathName = uiComponentManager.filePathTextField.text
        if (filePathName.isEmpty()) {
            showError("请选择文件")
            return
        }
        
        val filePath = Path.of(filePathName)
        if (!filePath.exists()) {
            showError("文件不存在")
            return
        }
        
        uiComponentManager.updateFileButton.isDisable = true
        val service = AssemblyAIService(config.apiKey)
        runBlocking {
            service.runCatching {
                updateFile(filePath)
            }.onFailure { e ->
                showError("上传文件失败: ${e.message}")
            }.onSuccess {
                showInfo("上传文件成功", "文件已上传至 $it")
                uiComponentManager.urlTextField.text = it
            }
        }
        uiComponentManager.updateFileButton.isDisable = false
    }

    private fun transcribeAudio() {
        val url = uiComponentManager.urlTextField.text
        if (url.isEmpty()) {
            showError("请输入URL")
            return
        }
        
        val service = AssemblyAIService(config.apiKey)
        runBlocking {
            service.runCatching {
                transcribeWithGeneralModel(url)
            }.onFailure {
                showError("转换失败: ${it.message}")
            }.onSuccess {
                showInfo("转换成功", "转换ID: $it")
            }
        }
    }

    private fun queryTranscription() {
        val service = AssemblyAIService(config.apiKey)
        runBlocking {
            service.runCatching {
                queryTranscription()
            }.onFailure { e ->
                showError("查询失败: ${e.message}")
            }.onSuccess { result ->
                uiComponentManager.resultListView.items = FXCollections.observableArrayList(result)
            }
        }
    }

    private fun getResult() {
        val selectedItem = uiComponentManager.resultListView.selectionModel.selectedItem
        if (selectedItem == null) {
            showWarn("未选择转换", "请先选择一个转换项目")
            return
        }
        
        val service = AssemblyAIService(config.apiKey)
        runBlocking {
            service.runCatching {
                getSentencesFromResult(selectedItem)
            }.onFailure { e ->
                showError("获取结果失败: ${e.message}")
            }.onSuccess { sentencesData ->
                sentencesMaker.setItems(sentencesData)
            }
        }
    }

    private fun saveResult() {
        val selectedItem = uiComponentManager.resultListView.selectionModel.selectedItem
        if (selectedItem == null) {
            showWarn("未选择转换", "请先选择一个转换项目")
            return
        }
        
        val service = AssemblyAIService(config.apiKey)
        runBlocking {
            service.runCatching {
                getSentencesFromResult(selectedItem)
            }.onFailure { e ->
                showError("获取结果失败: ${e.message}")
            }.onSuccess { sentencesData ->
                val fileChooser = FileChooser().apply {
                    title = "保存结果"
                    extensionFilters.addAll(
                        FileChooser.ExtensionFilter("JSON文件", "*.json"),
                        FileChooser.ExtensionFilter("所有文件", "*.*")
                    )
                }
                val fileToSave = fileChooser.showSaveDialog(currentStage)
                if (fileToSave != null) {
                    try {
                        fileToSave.writeText(Json.Default.encodeToString(sentencesData))
                        showInfo("成功", "结果已保存到 ${fileToSave.absolutePath}")
                    } catch (e: Exception) {
                        showError("保存失败: ${e.message}")
                    }
                }
            }
        }
    }

    private fun getResultFromFile() {
        val fileChooser = FileChooser().apply {
            title = "选择结果文件"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("JSON文件", "*.json"),
                FileChooser.ExtensionFilter("所有文件", "*.*")
            )
        }
        val selectedFile = fileChooser.showOpenDialog(currentStage)
        if (selectedFile != null) {
            try {
                val content = selectedFile.readText()
                sentences.clear()
                sentences.addAll(Json.Default.decodeFromString<List<Sentence>>(content))
                sentencesMaker.setItems(sentences)
                uiComponentManager.transcriptSegmentTableView.items = FXCollections.observableArrayList(sentences.flatMap { it.words })
            } catch (e: Exception) {
                showError("加载结果失败: ${e.message}")
            }
        }
    }

    private fun mergeSentences() {
        val selectedIndex = sentencesMaker.selectedIndex
        if (selectedIndex == -1) {
            showWarn("未选择句子", "请先选择一个句子")
            return
        }
        
        if (selectedIndex >= sentences.size - 1) {
            showWarn("无法合成", "所选句子已是最后一个，无法与下一个句子合成")
            return
        }
        
        // 获取选中句子和下一个句子
        val currentSentence = sentences[selectedIndex]
        val nextSentence = sentences[selectedIndex + 1]
        
        // 合并转录片段列表
        val mergedTranscriptSegment = (currentSentence.words + nextSentence.words).toMutableList()
        
        // 合并文本
        val mergedText = if (currentSentence.text.isNotEmpty() && nextSentence.text.isNotEmpty()) {
            "${currentSentence.words.joinToString(" ") { it.text }} ${nextSentence.words.joinToString(" ") { it.text }}"
        } else {
            (currentSentence.words + nextSentence.words).joinToString(" ") { it.text }
        }
        
        // 更新映射
        sentences.removeAt(selectedIndex + 1) // 删除下一个句子
        sentences[selectedIndex] = Sentence(
            text = mergedText,
            words = mergedTranscriptSegment
        ) // 更新当前句子
        
        // 更新UI
        sentencesMaker.getItems().removeAt(selectedIndex + 1) // 删除下一个句子显示
        sentencesMaker.getItems()[selectedIndex] = sentences[selectedIndex] // 更新当前句子显示
        
        // 更新转录片段表格
        val transcriptSegments = sentences.flatMap { it.words }.toMutableList()
        uiComponentManager.transcriptSegmentTableView.items = FXCollections.observableArrayList(transcriptSegments)
    }
    private fun splitSentence() {
        val selectedSentenceIndex = sentencesMaker.selectedIndex
        if (selectedSentenceIndex == -1) {
            showWarn("未选择句子", "请先选择一个句子")
            return
        }

        if (selectedSentenceIndex >= sentences.size) {
            showWarn("无效选择", "所选句子不存在")
            return
        }

        val selectedTranscriptSegmentIndex = uiComponentManager.transcriptSegmentTableView.selectionModel.selectedIndex
        if (selectedTranscriptSegmentIndex == -1) {
            showWarn("未选择转录片段", "请先选择一个转录片段作为分割点")
            return
        }

        val transcriptSegmentsOfCurrentSentence = sentences[selectedSentenceIndex]
        if (transcriptSegmentsOfCurrentSentence.words.size <= 1) {
            showWarn("无法分割", "所选句子只包含一个转录片段，无法分割")
            return
        }

        val selectedTranscriptSegment = uiComponentManager.transcriptSegmentTableView.items[selectedTranscriptSegmentIndex]
        val transcriptSegmentIndexInSentence = transcriptSegmentsOfCurrentSentence.words.indexOf(selectedTranscriptSegment)
        if (transcriptSegmentIndexInSentence <= 0 || transcriptSegmentIndexInSentence >= transcriptSegmentsOfCurrentSentence.words.size) {
            showWarn("无法分割", "所选转录片段不能用于分割句子")
            return
        }

        // 分割转录片段列表
        val firstHalf = Sentence(
            text = transcriptSegmentsOfCurrentSentence.words.subList(0, transcriptSegmentIndexInSentence)
                .joinToString(" ") { it.text },
            words = transcriptSegmentsOfCurrentSentence.words.subList(0, transcriptSegmentIndexInSentence)
        )
        val lastHalf = Sentence(
            text = transcriptSegmentsOfCurrentSentence.words.subList(
                transcriptSegmentIndexInSentence,
                transcriptSegmentsOfCurrentSentence.words.size
            ).joinToString(" ") { it.text },
            words = transcriptSegmentsOfCurrentSentence.words.subList(
                transcriptSegmentIndexInSentence,
                transcriptSegmentsOfCurrentSentence.words.size
            )
        )

        // 更新映射
        sentences[selectedSentenceIndex] = firstHalf // 更新当前句子为前半部分
        sentences.add(selectedSentenceIndex + 1, lastHalf) // 在当前位置后插入后半部分

        // 更新UI
        sentencesMaker.getItems()[selectedSentenceIndex] = firstHalf // 更新当前句子
        sentencesMaker.getItems().add(selectedSentenceIndex + 1, lastHalf) // 添加新句子

        // 更新转录片段表格
        val transcriptSegmentsData = sentences.flatMap { it.words }.toMutableList()
        uiComponentManager.transcriptSegmentTableView.items = FXCollections.observableArrayList(transcriptSegmentsData)
    }
    private fun playSelectedSentence() {
        if (uiComponentManager.filePathTextField.text.isEmpty()) {
            showWarn("未选择文件", "请先选择一个文件")
            return
        }
        
        val selectedSentenceIndex = sentencesMaker.selectedIndex
        if (selectedSentenceIndex == -1) {
            showWarn("未选择句子", "请先选择一个句子")
            return
        }
        Player.play(
            pathToPlay = Path.of(uiComponentManager.filePathTextField.text),
            startMilliseconds = sentences[selectedSentenceIndex].words.first().start,
            endMilliseconds = sentences[selectedSentenceIndex].words.last().end
        )
    }

    private fun saveAsSubtitleEvent() {
        if (sentences.isEmpty()) {
            showWarn("没有句子数据", "请先获取句子列表数据")
            return
        }
        
        val subtitleEvents = mutableListOf<String>()
        for (i in 0 until sentences.size) {
            val transcriptSegmentsOfCurrentSentence = sentences[i]
            if (transcriptSegmentsOfCurrentSentence.words.isEmpty()) continue
            subtitleEvents.add(sentencesMaker.getItems()[i].toSubtitleEvent().toString())
        }
        
        val fileChooser = FileChooser().apply {
            title = "保存字幕事件"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("所有文件", "*.*")
            )
        }
        val fileToSave = fileChooser.showSaveDialog(currentStage)
        if (fileToSave != null) {
            try {
                fileToSave.writeText(subtitleEvents.joinToString("\n"))
                showInfo("成功", "字幕事件已保存到 ${fileToSave.absolutePath}")
            } catch (e: Exception) {
                showError("保存失败: ${e.message}")
            }
        }
    }
    

    private fun initializeTranscriptSegmentTable() {
        uiComponentManager.transcriptSegmentTableView.columns.clear()
        
        uiComponentManager.transcriptSegmentTableView.columns.add(TableColumn<TranscriptSegment, String>("文本").apply {
            prefWidth = 150.0
            cellValueFactory = PropertyValueFactory<TranscriptSegment, String>("text")
            
            cellFactory = Callback { tableColumn ->
                object : TableCell<TranscriptSegment, String>() {
                    override fun updateItem(item: String?, empty: Boolean) {
                        super.updateItem(item, empty)
                        
                        text = if (empty) null else item
                        
                        style = ""
                        if (empty || item == null) return
                        val transcriptSegmentIndex = index
                        if (transcriptSegmentIndex < 0 || transcriptSegmentIndex >= uiComponentManager.transcriptSegmentTableView.items.size) return
                        val currentTranscriptSegment =
                            uiComponentManager.transcriptSegmentTableView.items[transcriptSegmentIndex]
                        val selectedSentence = sentencesMaker.selectedItem
                        if (selectedSentence == null) return
                        val sentenceIndex = sentencesMaker.getItems().indexOf(selectedSentence)
                        if (sentenceIndex < 0 || sentenceIndex >= sentences.size) return
                        val transcriptSegmentsOfSentence = sentences[sentenceIndex]
                        if (!transcriptSegmentsOfSentence.words.contains(currentTranscriptSegment)) return
                        style = "-fx-text-fill: red;"
                    }
                }
            }
        })
        
        uiComponentManager.transcriptSegmentTableView.columns.add(TableColumn<TranscriptSegment, Double>("置信度").apply {
            prefWidth = 100.0
            cellValueFactory = PropertyValueFactory<TranscriptSegment, Double>("confidence")
        })
        
        uiComponentManager.transcriptSegmentTableView.columns.add(TableColumn<TranscriptSegment, Int>("开始时间").apply {
            prefWidth = 100.0
            cellValueFactory = PropertyValueFactory<TranscriptSegment, Int>("start")
        })
        
        uiComponentManager.transcriptSegmentTableView.columns.add(TableColumn<TranscriptSegment, Int>("结束时间").apply {
            prefWidth = 100.0
            cellValueFactory = PropertyValueFactory<TranscriptSegment, Int>("end")
        })
    }

    private fun showError(message: String) {
        Alert(Alert.AlertType.ERROR).apply {
            title = "错误"
            headerText = "操作失败"
            contentText = message
            showAndWait()
        }
    }

    private fun showInfo(title: String, message: String) {
        Alert(Alert.AlertType.INFORMATION).apply {
            this.title = title
            headerText = title
            contentText = message
            showAndWait()
        }
    }

    private fun showWarn(title: String, message: String) {
        Alert(Alert.AlertType.WARNING).apply {
            this.title = title
            headerText = title
            contentText = message
            showAndWait()
        }
    }
}