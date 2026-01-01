package org.wiamotit1e

import javafx.application.Application
import javafx.collections.FXCollections
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.FileChooser
import javafx.stage.Stage
import javafx.scene.control.cell.PropertyValueFactory
import javafx.collections.ObservableList
import javafx.scene.control.TableColumn.CellDataFeatures
import javafx.util.Callback
import javafx.scene.control.cell.TextFieldTableCell
import javafx.scene.control.TableCell
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists

class JavaFX应用 : Application() {
    
    private var 配置: 配置 = 获取配置()
    
    private var 文本框: TextField? = null
    
    private var 显示密钥按钮: Button? = null
    
    private var 保存密钥按钮: Button? = null
    
    private var 文件路径文本框: TextField? = null
    
    private var 选择文件按钮: Button? = null
    
    private var 上传文件按钮: Button? = null
    
    private var url文本框: TextField? = null
    
    private var 转换按钮: Button? = null
    
    private var 查询按钮: Button? = null
    
    private var 结果列表: ListView<String>? = null
    
    private var 句子列表: ListView<String>? = null
    
    private var 转录片段表格: TableView<转录片段>? = null
    
    private var 获取结果按钮: Button? = null
    
    private var 保存结果按钮: Button? = null
    
    private var 从文件获取结果按钮: Button? = null
    
    override fun start(舞台: Stage?) {
        文本框 = TextField().apply {
            prefWidth = 400.0
        }
        显示密钥按钮 = Button("配置").apply {
            setOnAction {
                文本框!!.text = 配置.密钥
            }
        }
        
        保存密钥按钮 = Button("保存").apply {
            setOnAction {
                配置 = 配置.copy(密钥 = 文本框!!.text)
                保存配置(配置)
            }
        }
        
        val 密钥框 = VBox(
            Label("密钥:"),
            HBox(
                10.0,
                文本框,
                显示密钥按钮,
                保存密钥按钮
            )
        ).apply {
            spacing = 10.0
            padding = javafx.geometry.Insets(10.0)
        }
        
        文件路径文本框 = TextField().apply {
            prefWidth = 400.0
            isEditable = false
            promptText = "选择文件..."
        }
        
        选择文件按钮 = Button("选择文件").apply {
            setOnAction {
                val 文件选择器 = FileChooser().apply {
                    title = "选择文件"
                    extensionFilters.addAll(
                        FileChooser.ExtensionFilter("所有文件", "*.*"),
                    )
                }
                val 选中的文件 = 文件选择器.showOpenDialog(舞台)
                if (选中的文件 != null) {
                    文件路径文本框!!.text = 选中的文件.absolutePath
                }
            }
        }
        
        上传文件按钮 = Button("上传文件").apply {
            setOnAction {
                val 文件 = Path.of(文件路径文本框!!.text)
                if (!文件.exists()) {
                    Alert(Alert.AlertType.ERROR).apply {
                        title = "错误"
                        headerText = "文件不存在"
                        showAndWait()
                    }
                    return@setOnAction
                }
                isDisable = true
                val 服务 = AssemblyAI服务(配置.密钥)
                runBlocking {
                    服务.runCatching {
                        上传文件(文件)
                    }.onFailure { e ->
                        Alert(Alert.AlertType.ERROR).apply {
                            title = "错误"
                            headerText = "上传文件失败"
                            contentText = e.message
                            showAndWait()
                        }
                    }.onSuccess {
                        Alert(Alert.AlertType.INFORMATION).apply {
                            title = "提示"
                            headerText = "上传文件成功"
                            contentText = "文件已上传至 $it"
                            url文本框!!.text = it
                            showAndWait()
                        }
                    }
                }
                isDisable = false
            }
        }
        
        val 文件选择框 = VBox(
            Label("选择文件:"),
            HBox(
                10.0,
                文件路径文本框,
                选择文件按钮,
                上传文件按钮
            )
        ).apply {
            spacing = 10.0
            padding = javafx.geometry.Insets(10.0)
        }
        
        url文本框 = TextField().apply {
            prefWidth = 400.0
            text = ""
        }
        
        转换按钮 = Button("转换").apply {
            setOnAction {
                val 服务 = AssemblyAI服务(配置.密钥)
                runBlocking {
                    服务.runCatching {
                        使用通用模型转文字(url文本框!!.text)
                    }.onFailure {
                        Alert(Alert.AlertType.ERROR).apply {
                            title = "错误"
                            headerText = "转换失败"
                            contentText = it.message
                            showAndWait()
                        }
                    }
                }
            }
        }
        
        val 转换框 = VBox(
            Label("URL:"),
            HBox(
                10.0,
                url文本框,
                转换按钮
            )
        ).apply {
            spacing = 10.0
            padding = javafx.geometry.Insets(10.0)
        }
        
        查询按钮 = Button("查询转换").apply {
            setOnAction {
                val 服务 = AssemblyAI服务(配置.密钥)
                runBlocking {
                    服务.runCatching {
                        获取转换() // 这是您提到的 AssemblyAI 服务方法
                    }.onFailure { e ->
                        Alert(Alert.AlertType.ERROR).apply {
                            title = "错误"
                            headerText = "查询失败"
                            contentText = e.message
                            showAndWait()
                        }
                    }.onSuccess { 结果 ->
                        结果列表?.items = FXCollections.observableArrayList(结果)
                    }
                }
            }
        }
        
        结果列表 = ListView<String>().apply {
            prefHeight = 150.0
            prefWidth = 600.0
        }
        
        句子列表 = ListView<String>().apply {
            prefHeight = 200.0
            prefWidth = 600.0
        }
        
        val 句子转录片段映射 = mutableListOf<List<转录片段>>()
        
        转录片段表格 = TableView<转录片段>().apply {
            prefHeight = 200.0
            prefWidth = 600.0
            
            columns.add(TableColumn<转录片段, String>("文本").apply {
                prefWidth = 150.0
                cellValueFactory = PropertyValueFactory<转录片段, String>("text")
                
                cellFactory = Callback { tableColumn ->
                    object : TableCell<转录片段, String>() {
                        override fun updateItem(item: String?, empty: Boolean) {
                            super.updateItem(item, empty)
                            
                            if (empty || item == null) {
                                text = null
                                style = ""
                            } else {
                                text = item
                                val 转录片段索引 = index
                                if (转录片段索引 >= 0 && 转录片段索引 < 转录片段表格!!.items.size) {
                                    val 当前转录片段 = 转录片段表格!!.items[转录片段索引]
                                    val 选中的句子 = 句子列表?.selectionModel?.selectedItem
                                    
                                    if (选中的句子 != null) {
                                        val 句子索引 = 句子列表!!.items.indexOf(选中的句子)
                                        if (句子索引 >= 0 && 句子索引 < 句子转录片段映射.size) {
                                            val 对应转录片段列表 = 句子转录片段映射[句子索引]
                                            if (对应转录片段列表.contains(当前转录片段)) {
                                                style = "-fx-text-fill: red;"
                                            } else {
                                                style = ""
                                            }
                                        } else {
                                            style = ""
                                        }
                                    } else {
                                        style = ""
                                    }
                                } else {
                                    style = ""
                                }
                            }
                        }
                    }
                }
            })
            
            columns.add(TableColumn<转录片段, Double>("置信度").apply {
                prefWidth = 100.0
                cellValueFactory = PropertyValueFactory<转录片段, Double>("confidence")
            })
            
            columns.add(TableColumn<转录片段, Int>("开始时间").apply {
                prefWidth = 100.0
                cellValueFactory = PropertyValueFactory<转录片段, Int>("start")
            })
            
            columns.add(TableColumn<转录片段, Int>("结束时间").apply {
                prefWidth = 100.0
                cellValueFactory = PropertyValueFactory<转录片段, Int>("end")
            })
        }
        
        // 监听句子列表选择变化，自动跳转到对应转录片段
        句子列表!!.selectionModel.selectedItemProperty().addListener { _, _, 新选择 ->
            if (新选择 != null) {
                val 句子索引 = 句子列表!!.items.indexOf(新选择)
                if (句子索引 >= 0 && 句子索引 < 句子转录片段映射.size) {
                    val 对应转录片段 = 句子转录片段映射[句子索引]
                    if (对应转录片段.isNotEmpty()) {
                        val 第一个转录片段 = 对应转录片段[0]
                        val 片段索引 = 转录片段表格!!.items.indexOf(第一个转录片段)
                        if (片段索引 >= 0) {
                            转录片段表格!!.selectionModel.select(片段索引)
                            转录片段表格!!.scrollTo(片段索引)
                        }
                    }
                }
            }
            
            // 刷新转录片段表格以更新文本颜色
            转录片段表格?.refresh()
        }
        
        获取结果按钮 = Button("获取结果").apply {
            setOnAction {
                val 选中的项目 = 结果列表?.selectionModel?.selectedItem
                if (选中的项目 == null) {
                    Alert(Alert.AlertType.WARNING).apply {
                        title = "警告"
                        headerText = "未选择转换"
                        contentText = "请先选择一个转换项目"
                        showAndWait()
                    }
                    return@setOnAction
                }
                
                val 服务 = AssemblyAI服务(配置.密钥)
                runBlocking {
                    服务.runCatching {
                        在结果中获取句子列表(选中的项目)
                    }.onFailure { e ->
                        Alert(Alert.AlertType.ERROR).apply {
                            title = "错误"
                            headerText = "获取结果失败"
                            contentText = e.message
                            showAndWait()
                        }
                    }.onSuccess { 句子列表_数据 ->
                        句子列表?.items?.clear()
                        转录片段表格?.items?.clear()
                        句子转录片段映射.clear() // 清空映射关系
                        
                        句子列表_数据.forEach { 句子 ->
                            句子列表?.items?.add("文本: ${句子.text}")
                            句子转录片段映射.add(句子.words) // 存储映射关系
                        }
                        
                        // 设置转录片段表格数据
                        val 转录片段数据 = mutableListOf<转录片段>()
                        句子列表_数据.forEach { 句子 ->
                            句子.words.forEach { 片段 ->
                                转录片段数据.add(片段)
                            }
                        }
                        转录片段表格?.items = FXCollections.observableArrayList(转录片段数据)
                        
                        // 刷新表格以更新颜色
                        转录片段表格?.refresh()
                    }
                }
            }
        }
        
        保存结果按钮 = Button("保存结果").apply {
            setOnAction {
                val 选中的项目 = 结果列表?.selectionModel?.selectedItem
                if (选中的项目 == null) {
                    Alert(Alert.AlertType.WARNING).apply {
                        title = "警告"
                        headerText = "未选择转换"
                        contentText = "请先选择一个转换项目"
                        showAndWait()
                    }
                    return@setOnAction
                }
                
                val 服务 = AssemblyAI服务(配置.密钥)
                runBlocking {
                    服务.runCatching {
                        在结果中获取句子列表(选中的项目)
                    }.onFailure { e ->
                        Alert(Alert.AlertType.ERROR).apply {
                            title = "错误"
                            headerText = "获取结果失败"
                            contentText = e.message
                            showAndWait()
                        }
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
                                Alert(Alert.AlertType.INFORMATION).apply {
                                    title = "成功"
                                    headerText = "结果已保存"
                                    contentText = "结果已保存到 ${保存文件.absolutePath}"
                                    showAndWait()
                                }
                            } catch (e: Exception) {
                                Alert(Alert.AlertType.ERROR).apply {
                                    title = "错误"
                                    headerText = "保存失败"
                                    contentText = e.message
                                    showAndWait()
                                }
                            }
                        }
                    }
                }
            }
        }
        
        从文件获取结果按钮 = Button("从文件获取结果").apply {
            setOnAction {
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
                        
                        句子列表?.items?.clear()
                        转录片段表格?.items?.clear()
                        句子转录片段映射.clear() // 清空映射关系
                        
                        句子列表_数据.forEach { 句子 ->
                            句子列表?.items?.add("文本: ${句子.text}")
                            句子转录片段映射.add(句子.words) // 存储映射关系
                        }
                        
                        // 设置转录片段表格数据
                        val 转录片段数据 = mutableListOf<转录片段>()
                        句子列表_数据.forEach { 句子 ->
                            句子.words.forEach { 片段 ->
                                转录片段数据.add(片段)
                            }
                        }
                        转录片段表格?.items = FXCollections.observableArrayList(转录片段数据)
                        
                        // 刷新表格以更新颜色
                        转录片段表格?.refresh()
                    } catch (e: Exception) {
                        Alert(Alert.AlertType.ERROR).apply {
                            title = "错误"
                            headerText = "加载结果失败"
                            contentText = e.message
                            showAndWait()
                        }
                    }
                }
            }
        }
        
        val 句子和转录片段框 = VBox(10.0).apply {
            children.addAll(
                VBox(Label("句子列表:"), 句子列表!!).apply { spacing = 10.0 },
                VBox(Label("转录片段表格:"), 转录片段表格!!).apply { spacing = 10.0 }
            )
        }
        
        // 添加合成句子和分割句子按钮
        val 合成句子按钮 = Button("合成句子").apply {
            setOnAction {
                val 选中句子索引 = 句子列表!!.selectionModel.selectedIndex
                if (选中句子索引 == -1) {
                    Alert(Alert.AlertType.WARNING).apply {
                        title = "警告"
                        headerText = "未选择句子"
                        contentText = "请先选择一个句子"
                        showAndWait()
                    }
                    return@setOnAction
                }
                
                if (选中句子索引 >= 句子转录片段映射.size - 1) {
                    Alert(Alert.AlertType.WARNING).apply {
                        title = "警告"
                        headerText = "无法合成"
                        contentText = "所选句子已是最后一个，无法与下一个句子合成"
                        showAndWait()
                    }
                    return@setOnAction
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
                句子列表?.items?.removeAt(选中句子索引 + 1) // 删除下一个句子显示
                句子列表?.items?.set(选中句子索引, "文本: $合并后文本") // 更新当前句子显示
                
                // 更新转录片段表格
                val 转录片段数据 = mutableListOf<转录片段>()
                句子转录片段映射.forEach { 片段列表 ->
                    片段列表.forEach { 片段 ->
                        转录片段数据.add(片段)
                    }
                }
                转录片段表格?.items = FXCollections.observableArrayList(转录片段数据)
                
                // 刷新表格以更新颜色
                转录片段表格?.refresh()
            }
        }
        
        val 分割句子按钮 = Button("分割句子").apply {
            setOnAction {
                val 选中句子索引 = 句子列表!!.selectionModel.selectedIndex
                if (选中句子索引 == -1) {
                    Alert(Alert.AlertType.WARNING).apply {
                        title = "警告"
                        headerText = "未选择句子"
                        contentText = "请先选择一个句子"
                        showAndWait()
                    }
                    return@setOnAction
                }
                
                if (选中句子索引 >= 句子转录片段映射.size) {
                    Alert(Alert.AlertType.WARNING).apply {
                        title = "警告"
                        headerText = "无效选择"
                        contentText = "所选句子不存在"
                        showAndWait()
                    }
                    return@setOnAction
                }
                
                val 选中转录片段索引 = 转录片段表格!!.selectionModel.selectedIndex
                if (选中转录片段索引 == -1) {
                    Alert(Alert.AlertType.WARNING).apply {
                        title = "警告"
                        headerText = "未选择转录片段"
                        contentText = "请先选择一个转录片段作为分割点"
                        showAndWait()
                    }
                    return@setOnAction
                }
                
                val 当前句子转录片段 = 句子转录片段映射[选中句子索引]
                if (当前句子转录片段.size <= 1) {
                    Alert(Alert.AlertType.WARNING).apply {
                        title = "警告"
                        headerText = "无法分割"
                        contentText = "所选句子只包含一个转录片段，无法分割"
                        showAndWait()
                    }
                    return@setOnAction
                }
                
                // 找到选中转录片段在当前句子中的索引
                val 句子内转录片段索引 = 当前句子转录片段.indexOf(转录片段表格!!.items[选中转录片段索引])
                if (句子内转录片段索引 <= 0 || 句子内转录片段索引 >= 当前句子转录片段.size) {
                    Alert(Alert.AlertType.WARNING).apply {
                        title = "警告"
                        headerText = "无法分割"
                        contentText = "所选转录片段不能用于分割句子"
                        showAndWait()
                    }
                    return@setOnAction
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
                句子列表?.items?.set(选中句子索引, "文本: $前半句文本") // 更新当前句子
                句子列表?.items?.add(选中句子索引 + 1, "文本: $后半句文本") // 添加新句子
                
                // 更新转录片段表格
                val 转录片段数据 = mutableListOf<转录片段>()
                句子转录片段映射.forEach { 片段列表 ->
                    片段列表.forEach { 片段 ->
                        转录片段数据.add(片段)
                    }
                }
                转录片段表格?.items = FXCollections.observableArrayList(转录片段数据)
                
                // 刷新表格以更新颜色
                转录片段表格?.refresh()
            }
        }
        
        val 句子操作按钮框 = HBox(10.0, 合成句子按钮, 分割句子按钮)
        
        val 保存为字幕事件按钮 = Button("保存为字幕事件").apply {
            setOnAction {
                // 检查是否有句子数据
                if (句子转录片段映射.isEmpty()) {
                    Alert(Alert.AlertType.WARNING).apply {
                        title = "警告"
                        headerText = "没有句子数据"
                        contentText = "请先获取句子列表数据"
                        showAndWait()
                    }
                    return@setOnAction
                }
                
                // 创建所有字幕事件
                val 字幕事件列表 = mutableListOf<String>()
                for (i in 0 until 句子转录片段映射.size) {
                    val 当前句子转录片段 = 句子转录片段映射[i]
                    if (当前句子转录片段.isNotEmpty()) {
                        // 获取当前句子的文本
                        val 句子文本 = 句子列表!!.items[i]
                        val 纯文本 = 句子文本.substringAfter("文本: ")
                        
                        // 创建句子对象用于转换为字幕事件
                        val 句子对象 = 句子(text = 纯文本, words = 当前句子转录片段)
                        val 字幕事件 = 句子对象.字幕事件()
                        
                        字幕事件列表.add(字幕事件.toString())
                    }
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
                        Alert(Alert.AlertType.INFORMATION).apply {
                            title = "成功"
                            headerText = "字幕事件已保存"
                            contentText = "字幕事件已保存到 ${保存文件.absolutePath}"
                            showAndWait()
                        }
                    } catch (e: Exception) {
                        Alert(Alert.AlertType.ERROR).apply {
                            title = "错误"
                            headerText = "保存失败"
                            contentText = e.message
                            showAndWait()
                        }
                    }
                }
            }
        }
        
        val 查询框 = VBox(
            Label("转换查询:"),
            HBox(10.0, 查询按钮, 获取结果按钮, 保存结果按钮, 从文件获取结果按钮),
            结果列表,
            句子和转录片段框,
            句子操作按钮框,
            HBox(10.0, 保存为字幕事件按钮)
        ).apply {
            spacing = 10.0
            padding = javafx.geometry.Insets(10.0)
        }
        
        
        舞台!!.apply {
            title = "Wiam字幕制作器"
            scene = Scene(
                VBox(
                    密钥框,
                    文件选择框,
                    转换框,
                    查询框,
                ).apply {
                    padding = javafx.geometry.Insets(20.0)
                },
                800.0,
                1000.0
            )
            show()
        }
    }
}