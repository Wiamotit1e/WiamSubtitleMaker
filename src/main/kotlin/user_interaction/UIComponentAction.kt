package org.wiamotit1e.user_interaction

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.wiamotit1e.*
import java.io.File

class UIComponentAction(
    val data: UIComponentData = UIComponentData(),
    val selectFile: (String, List<String>) -> File?,
    val saveFile: (String, List<String>) -> File?
) {
    
    val assemblyAIService = AssemblyAIService(apiKey = getConfig().apiKey)
    
    val sentenceCache = mutableMapOf<String, List<Sentence>>()
    
    fun onApiKeyDisplayButton() {
        data.apiKey.set(getConfig().apiKey)
    }
    
    fun onApiKeySaveButton() {
        saveConfig(config = Config(apiKey = data.apiKey.get()))
    }
    
    fun onSelectFileButton(): Message {
        val file = selectFile("选择文件", listOf("*.*"))
        if (file == null) return Message.Success("取消选择")
        data.filePath.set(file.absolutePath)
        return Message.Success("选择文件成功")
    }
    
    suspend fun onUpdateFileButton(): Message {
        if (data.filePath.get().isEmpty()) return Message.Warning("请选择文件")
        val file = File(data.filePath.get())
        if (!file.exists()) return Message.Warning("文件不存在")
        assemblyAIService
            .runCatching { updateFile(file.toPath()) }
            .onSuccess {
                data.url.set(it)
                return Message.Info("上传成功")
            }
        return Message.Failure("上传失败")
    }
    
    suspend fun onTranscribeButton(): Message {
        assemblyAIService
            .runCatching { transcribeWithGeneralModel(url = data.url.get()) }
            .onSuccess {
                data.results.addAll(it)
                return Message.Info("转录成功")
            }
        return Message.Failure("转录失败")
    }
    
    suspend fun onQueryButton(): Message {
        assemblyAIService
            .runCatching { queryTranscription() }
            .onSuccess {
                it.forEach { data.results.add(it) }
                return Message.Info("查询成功")
            }
        return Message.Failure("查询失败")
    }
    
    suspend fun onGetResultButton(): Message {
        if (data.selectedResult.get().isEmpty()) return Message.Warning("请先选择结果")
        val var1 = data.selectedResult.get()
        assemblyAIService
            .runCatching { getSentencesFromResult(id = var1) }
            .onSuccess {
                data.sentences.setAll(it)
                sentenceCache.put(var1, it)
                data.transcriptSegments.setAll(it.flatMap { it.words })
                return Message.Info("查询成功")
            }
        return Message.Failure("查询失败")
    }
    
    fun onSaveResultButton(): Message {
        if (data.selectedResult.get().isEmpty()) return Message.Warning("请先选择结果")
        val var1 = data.selectedResult.get()
        val var2 = sentenceCache[var1]
        if (var2 == null) return Message.Warning("请先获取结果")
        val file = saveFile("保存结果", listOf("*.json","*.txt"))
        if (file == null) return Message.Success("取消保存")
        file.runCatching { this.writeText(Json.Default.encodeToString(var2)) }
            .onSuccess { return Message.Info("保存成功") }
        return Message.Failure("保存失败")
    }
    
    fun onGetResultFromFileButton(): Message {
        val file = selectFile("选择文件", listOf("*.json","*.txt"))
        if (file == null) return Message.Success("取消选择")
        file.runCatching { this.readText() }
            .onSuccess {
                it.let { string ->
                    val sentences = Json.Default.decodeFromString<List<Sentence>>(string)
                    data.sentences.setAll(sentences)
                    data.transcriptSegments.addAll(sentences.flatMap { it.words })
                }
                return Message.Info("读取文件成功")
            }
        return Message.Failure("读取文件失败")
    }
    
    fun onMergeSentencesButton(): Message {
        if (data.selectedSentenceIndex.get() >= data.sentences.size - 1) return Message.Warning("没有可合并的句子")
        data.sentences[data.selectedSentenceIndex.get()] = data.sentences[data.selectedSentenceIndex.get()].apply {
            merge(data.sentences[data.selectedSentenceIndex.get() + 1])
        }
        data.sentences.removeAt(data.selectedSentenceIndex.get() + 1)
        return Message.Success("合并成功")
    }
    
    fun onSplitSentenceButton(): Message {
        val index = data.selectedSentenceIndex.get()
        val first: Int = data.sentences.take(index).sumOf { it.words.size }
         data.sentences[index].runCatching {
             val (s1, s2) = split(data.selectedTranscriptSegmentIndex.get() - first)
             if (data.sentences.size <= 1) {
                 data.sentences[index] = s1
                 data.sentences.add(s2)
             } else {
                 data.sentences[index] = s1
                 data.sentences.add(index + 1, s2)
             }
        }.onFailure { return Message.Warning("这个位置不可分割 $it") }
        return Message.Success("分割成功")
    }
    
    fun onPlaySelectedSentenceButton(): Message {
        if (data.filePath.get().isEmpty()) return Message.Warning("请选择文件")
        if (data.selectedSentenceIndex.get() < 0) return Message.Warning("请选择句子")
        val sentence = data.sentences[data.selectedSentenceIndex.get()]
        Player.runCatching {
            play(
                pathToPlay = File(data.filePath.get()).toPath(),
                startMilliseconds = sentence.words.first().start,
                endMilliseconds = sentence.words.last().end
            ) }
            .onFailure { return Message.Failure("播放失败") }
        return Message.Success("播放成功")
    }
    
    fun onSaveAsSubtitleEventButton(): Message {
        if (data.sentences.isEmpty()) return Message.Warning("没有句子数据")
        val file = saveFile("保存为 .ass 文件", listOf("*.ass"))
        if ( file == null) return Message.Success("取消保存")
        file.runCatching { this.writeText(AssContentGenerator.generate(data.sentences.map { it.toSubtitleEvent() })) }
            .onFailure { return Message.Failure("保存失败") }
         return Message.Info("保存成功")
    }
}