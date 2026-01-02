package org.wiamotit1e

import javafx.collections.FXCollections
import javafx.scene.control.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback
import javafx.scene.control.TableCell
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.text.substringAfter

/**
 * 业务逻辑处理器 - 处理所有业务逻辑，与UI组件解耦
 */
class 业务逻辑处理器(
    private val ui组件管理器: UI组件管理器,
    private val 舞台: Stage
) {
    private var 配置: 配置 = 获取配置()
    private val 句子转录片段映射 = mutableListOf<List<转录片段>>()
    
    fun 初始化事件处理器() {
        // 配置相关事件
        ui组件管理器.显示密钥按钮.setOnAction {
            显示密钥()
        }
        
        ui组件管理器.保存密钥按钮.setOnAction {
            保存密钥()
        }
        
        // 文件选择相关事件
        ui组件管理器.选择文件按钮.setOnAction {
            选择文件()
        }
        
        ui组件管理器.上传文件按钮.setOnAction {
            上传文件()
        }
        
        // 转换相关事件
        ui组件管理器.转换按钮.setOnAction {
            转换音频()
        }
        
        // 查询相关事件
        ui组件管理器.查询按钮.setOnAction {
            查询转换()
        }
        
        ui组件管理器.获取结果按钮.setOnAction {
            获取结果()
        }
        
        ui组件管理器.保存结果按钮.setOnAction {
            保存结果()
        }
        
        ui组件管理器.从文件获取结果按钮.setOnAction {
            从文件获取结果()
        }
        
        // 句子操作相关事件
        ui组件管理器.合成句子按钮.setOnAction {
            合成句子()
        }
        
        ui组件管理器.分割句子按钮.setOnAction {
            分割句子()
        }
        
        ui组件管理器.播放选中句子按钮.setOnAction {
            播放选中句子()
        }
        
        ui组件管理器.保存为字幕事件按钮.setOnAction {
            保存为字幕事件()
        }
        
        初始化句子列表()
        
        初始化转录片段表格()
        
        监听句子列表选择变化()
    }

    private fun 显示密钥() {
        ui组件管理器.文本框.text = 配置.密钥
    }

    private fun 保存密钥() {
        配置 = 配置.copy(密钥 = ui组件管理器.文本框.text)
        保存配置(配置)
    }

    private fun 选择文件() {
        val 文件选择器 = FileChooser().apply {
            title = "选择文件"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("所有文件", "*.*"),
            )
        }
        val 选中的文件 = 文件选择器.showOpenDialog(舞台)
        if (选中的文件 != null) {
            ui组件管理器.文件路径文本框.text = 选中的文件.absolutePath
        }
    }

    private fun 上传文件() {
        val 文件路径 = ui组件管理器.文件路径文本框.text
        if (文件路径.isEmpty()) {
            显示错误("请选择文件")
            return
        }
        
        val 文件 = Path.of(文件路径)
        if (!文件.exists()) {
            显示错误("文件不存在")
            return
        }
        
        ui组件管理器.上传文件按钮.isDisable = true
        val 服务 = AssemblyAI服务(配置.密钥)
        runBlocking {
            服务.runCatching {
                上传文件(文件)
            }.onFailure { e ->
                显示错误("上传文件失败: ${e.message}")
            }.onSuccess {
                显示信息("上传文件成功", "文件已上传至 $it")
                ui组件管理器.url文本框.text = it
            }
        }
        ui组件管理器.上传文件按钮.isDisable = false
    }

    private fun 转换音频() {
        val url = ui组件管理器.url文本框.text
        if (url.isEmpty()) {
            显示错误("请输入URL")
            return
        }
        
        val 服务 = AssemblyAI服务(配置.密钥)
        runBlocking {
            服务.runCatching {
                使用通用模型转文字(url)
            }.onFailure {
                显示错误("转换失败: ${it.message}")
            }.onSuccess {
                显示信息("转换成功", "转换ID: $it")
            }
        }
    }

    private fun 查询转换() {
        val 服务 = AssemblyAI服务(配置.密钥)
        runBlocking {
            服务.runCatching {
                获取转换()
            }.onFailure { e ->
                显示错误("查询失败: ${e.message}")
            }.onSuccess { 结果 ->
                ui组件管理器.结果列表.items = FXCollections.observableArrayList(结果)
            }
        }
    }

    private fun 获取结果() {
        val 选中的项目 = ui组件管理器.结果列表.selectionModel.selectedItem
        if (选中的项目 == null) {
            显示警告("未选择转换", "请先选择一个转换项目")
            return
        }
        
        val 服务 = AssemblyAI服务(配置.密钥)
        runBlocking {
            服务.runCatching {
                在结果中获取句子列表(选中的项目)
            }.onFailure { e ->
                显示错误("获取结果失败: ${e.message}")
            }.onSuccess { 句子列表_数据 ->
                句子列表_数据.更新UI()
            }
        }
    }

    private fun 保存结果() {
        val 选中的项目 = ui组件管理器.结果列表.selectionModel.selectedItem
        if (选中的项目 == null) {
            显示警告("未选择转换", "请先选择一个转换项目")
            return
        }
        
        val 服务 = AssemblyAI服务(配置.密钥)
        runBlocking {
            服务.runCatching {
                在结果中获取句子列表(选中的项目)
            }.onFailure { e ->
                显示错误("获取结果失败: ${e.message}")
            }.onSuccess { 句子列表_数据 ->
                val 文件选择器 = FileChooser().apply {
                    title = "保存结果"
                    extensionFilters.addAll(
                        FileChooser.ExtensionFilter("JSON文件", "*.json"),
                        FileChooser.ExtensionFilter("所有文件", "*.*")
                    )
                }
                val 保存文件 = 文件选择器.showSaveDialog(舞台)
                if (保存文件 != null) {
                    try {
                        保存文件.writeText(Json.encodeToString(句子列表_数据))
                        显示信息("成功", "结果已保存到 ${保存文件.absolutePath}")
                    } catch (e: Exception) {
                        显示错误("保存失败: ${e.message}")
                    }
                }
            }
        }
    }

    private fun 从文件获取结果() {
        val 文件选择器 = FileChooser().apply {
            title = "选择结果文件"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("JSON文件", "*.json"),
                FileChooser.ExtensionFilter("所有文件", "*.*")
            )
        }
        val 选中的文件 = 文件选择器.showOpenDialog(舞台)
        if (选中的文件 != null) {
            try {
                val 内容 = 选中的文件.readText()
                val 句子列表_数据 = Json.decodeFromString<List<句子>>(内容)
                句子列表_数据.更新UI()
            } catch (e: Exception) {
                显示错误("加载结果失败: ${e.message}")
            }
        }
    }

    private fun 合成句子() {
        val 选中句子索引 = ui组件管理器.句子列表.selectionModel.selectedIndex
        if (选中句子索引 == -1) {
            显示警告("未选择句子", "请先选择一个句子")
            return
        }
        
        if (选中句子索引 >= 句子转录片段映射.size - 1) {
            显示警告("无法合成", "所选句子已是最后一个，无法与下一个句子合成")
            return
        }
        
        // 获取选中句子和下一个句子
        val 当前句子 = 句子转录片段映射[选中句子索引]
        val 下一个句子 = 句子转录片段映射[选中句子索引 + 1]
        
        // 合并转录片段列表
        val 合并后转录片段 = (当前句子 + 下一个句子).toMutableList()
        
        // 合并文本
        val 合并后文本 = if (当前句子.isNotEmpty() && 下一个句子.isNotEmpty()) {
            "${当前句子.joinToString(" ") { it.text }} ${下一个句子.joinToString(" ") { it.text }}"
        } else {
            (当前句子 + 下一个句子).joinToString(" ") { it.text }
        }
        
        // 更新映射
        句子转录片段映射.removeAt(选中句子索引 + 1) // 删除下一个句子
        句子转录片段映射[选中句子索引] = 合并后转录片段 // 更新当前句子
        
        // 更新UI
        ui组件管理器.句子列表.items.removeAt(选中句子索引 + 1) // 删除下一个句子显示
        ui组件管理器.句子列表.items[选中句子索引] = "文本: $合并后文本" // 更新当前句子显示
        
        // 更新转录片段表格
        val 转录片段数据 = 句子转录片段映射.flatMap { it }.toMutableList()
        ui组件管理器.转录片段表格.items = FXCollections.observableArrayList(转录片段数据)
    }

    private fun 分割句子() {
        val 选中句子索引 = ui组件管理器.句子列表.selectionModel.selectedIndex
        if (选中句子索引 == -1) {
            显示警告("未选择句子", "请先选择一个句子")
            return
        }
        
        if (选中句子索引 >= 句子转录片段映射.size) {
            显示警告("无效选择", "所选句子不存在")
            return
        }
        
        val 选中转录片段索引 = ui组件管理器.转录片段表格.selectionModel.selectedIndex
        if (选中转录片段索引 == -1) {
            显示警告("未选择转录片段", "请先选择一个转录片段作为分割点")
            return
        }
        
        val 当前句子转录片段 = 句子转录片段映射[选中句子索引]
        if (当前句子转录片段.size <= 1) {
            显示警告("无法分割", "所选句子只包含一个转录片段，无法分割")
            return
        }
        
        // 找到选中转录片段在当前句子中的索引
        val 转录片段项 = ui组件管理器.转录片段表格.items[选中转录片段索引]
        val 句子内转录片段索引 = 当前句子转录片段.indexOf(转录片段项)
        if (句子内转录片段索引 <= 0 || 句子内转录片段索引 >= 当前句子转录片段.size) {
            显示警告("无法分割", "所选转录片段不能用于分割句子")
            return
        }
        
        // 分割转录片段列表
        val 前半部分 = 当前句子转录片段.subList(0, 句子内转录片段索引)
        val 后半部分 = 当前句子转录片段.subList(句子内转录片段索引, 当前句子转录片段.size)
        
        // 创建新的句子文本
        val 前半句文本 = 前半部分.joinToString(" ") { it.text }
        val 后半句文本 = 后半部分.joinToString(" ") { it.text }
        
        // 更新映射
        句子转录片段映射[选中句子索引] = 前半部分.toList() // 更新当前句子为前半部分
        句子转录片段映射.add(选中句子索引 + 1, 后半部分.toList()) // 在当前位置后插入后半部分
        
        // 更新UI
        ui组件管理器.句子列表.items[选中句子索引] = "文本: $前半句文本" // 更新当前句子
        ui组件管理器.句子列表.items.add(选中句子索引 + 1, "文本: $后半句文本") // 添加新句子
        
        // 更新转录片段表格
        val 转录片段数据 = 句子转录片段映射.flatMap { it }.toMutableList()
        ui组件管理器.转录片段表格.items = FXCollections.observableArrayList(转录片段数据)
    }
    
    private fun 播放选中句子() {
        if (ui组件管理器.文件路径文本框.text.isEmpty()) {
            显示警告("未选择文件", "请先选择一个文件")
            return
        }
        
        val 选中句子索引 = ui组件管理器.句子列表.selectionModel.selectedIndex
        if (选中句子索引 == -1) {
            显示警告("未选择句子", "请先选择一个句子")
            return
        }
        播放器.play(
            路径 = Path.of(ui组件管理器.文件路径文本框.text),
            开始毫秒数 = 句子转录片段映射[选中句子索引].first().start,
            结束毫秒数 = 句子转录片段映射[选中句子索引].last().end
        )
    }

    private fun 保存为字幕事件() {
        if (句子转录片段映射.isEmpty()) {
            显示警告("没有句子数据", "请先获取句子列表数据")
            return
        }
        
        val 字幕事件列表 = mutableListOf<String>()
        for (i in 0 until 句子转录片段映射.size) {
            val 当前句子转录片段 = 句子转录片段映射[i]
            if (当前句子转录片段.isEmpty()) continue
            val 句子文本 = ui组件管理器.句子列表.items[i]
            val 纯文本 = 句子文本.substringAfter("文本: ")
            字幕事件列表.add(句子(text = 纯文本, words = 当前句子转录片段).字幕事件().toString())
        }
        
        val 文件选择器 = FileChooser().apply {
            title = "保存字幕事件"
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("所有文件", "*.*")
            )
        }
        val 保存文件 = 文件选择器.showSaveDialog(舞台)
        if (保存文件 != null) {
            try {
                保存文件.writeText(字幕事件列表.joinToString("\n"))
                显示信息("成功", "字幕事件已保存到 ${保存文件.absolutePath}")
            } catch (e: Exception) {
                显示错误("保存失败: ${e.message}")
            }
        }
    }
    
    private fun 初始化句子列表() {
        ui组件管理器.句子列表.cellFactory = Callback {
            object : ListCell<String>() {
                override fun updateItem(item: String?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty) null else item
                    if(text.isNullOrEmpty()) return
                    style = if(text.substringAfter("文本: ").length > 80) "-fx-text-fill: red;" else ""
                }
            }
        }
    }

    private fun 初始化转录片段表格() {
        ui组件管理器.转录片段表格.columns.clear()
        
        ui组件管理器.转录片段表格.columns.add(TableColumn<转录片段, String>("文本").apply {
            prefWidth = 150.0
            cellValueFactory = PropertyValueFactory<转录片段, String>("text")
            
            cellFactory = Callback { tableColumn ->
                object : TableCell<转录片段, String>() {
                    override fun updateItem(item: String?, empty: Boolean) {
                        super.updateItem(item, empty)
                        
                        text = if (empty) null else item
                        
                        style = ""
                        if (empty || item == null) return
                        val 转录片段索引 = index
                        if (转录片段索引 < 0 || 转录片段索引 >= ui组件管理器.转录片段表格.items.size) return
                        val 当前转录片段 = ui组件管理器.转录片段表格.items[转录片段索引]
                        val 选中的句子 = ui组件管理器.句子列表.selectionModel.selectedItem
                        if (选中的句子 == null) return
                        val 句子索引 = ui组件管理器.句子列表.items.indexOf(选中的句子)
                        if (句子索引 < 0 || 句子索引 >= 句子转录片段映射.size) return
                        val 对应转录片段列表 = 句子转录片段映射[句子索引]
                        if (!对应转录片段列表.contains(当前转录片段)) return
                        style = "-fx-text-fill: red;"
                    }
                }
            }
        })
        
        ui组件管理器.转录片段表格.columns.add(TableColumn<转录片段, Double>("置信度").apply {
            prefWidth = 100.0
            cellValueFactory = PropertyValueFactory<转录片段, Double>("confidence")
        })
        
        ui组件管理器.转录片段表格.columns.add(TableColumn<转录片段, Int>("开始时间").apply {
            prefWidth = 100.0
            cellValueFactory = PropertyValueFactory<转录片段, Int>("start")
        })
        
        ui组件管理器.转录片段表格.columns.add(TableColumn<转录片段, Int>("结束时间").apply {
            prefWidth = 100.0
            cellValueFactory = PropertyValueFactory<转录片段, Int>("end")
        })
    }
    
    private fun 监听句子列表选择变化() {
        ui组件管理器.句子列表.selectionModel.selectedItemProperty().addListener { _, _, 新选择 ->
            if (新选择 == null) return@addListener
            
            val 句子索引 = ui组件管理器.句子列表.items.indexOf(新选择)
            if (句子索引 < 0 || 句子索引 >= 句子转录片段映射.size) return@addListener
            val 对应转录片段 = 句子转录片段映射[句子索引]
            if (对应转录片段.isEmpty()) return@addListener
            val 第一个转录片段 = 对应转录片段[0]
            val 片段索引 = ui组件管理器.转录片段表格.items.indexOf(第一个转录片段)
            if (片段索引 < 0) return@addListener
            ui组件管理器.转录片段表格.selectionModel.select(片段索引)
            ui组件管理器.转录片段表格.scrollTo(片段索引)
            ui组件管理器.转录片段表格.refresh()
        }
    }

    private fun List<句子>.更新UI() {
        ui组件管理器.句子列表.items.clear()
        ui组件管理器.转录片段表格.items.clear()
        句子转录片段映射.clear()
        
        this.forEach { 句子 ->
            ui组件管理器.句子列表.items.add("文本: ${句子.text}")
            句子转录片段映射.add(句子.words)
            
            val 转录片段数据 = this.flatMap { it.words }.toMutableList()
            ui组件管理器.转录片段表格.items = FXCollections.observableArrayList(转录片段数据)
        }
    }

    private fun 显示错误(消息: String) {
        Alert(Alert.AlertType.ERROR).apply {
            title = "错误"
            headerText = "操作失败"
            contentText = 消息
            showAndWait()
        }
    }

    private fun 显示信息(标题: String, 消息: String) {
        Alert(Alert.AlertType.INFORMATION).apply {
            title = 标题
            headerText = 标题
            contentText = 消息
            showAndWait()
        }
    }

    private fun 显示警告(标题: String, 消息: String) {
        Alert(Alert.AlertType.WARNING).apply {
            title = 标题
            headerText = 标题
            contentText = 消息
            showAndWait()
        }
    }
}