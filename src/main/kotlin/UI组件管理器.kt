package org.wiamotit1e

import javafx.scene.control.*
import javafx.scene.layout.*

/**
 * UI组件管理器 - 管理所有UI组件的创建和初始化
 */
class UI组件管理器 {
    
    val 文本框: TextField = TextField().apply {
        prefWidth = 400.0
    }
    
    val 显示密钥按钮: Button = Button("配置").apply {
        text = "配置"
    }
    
    val 保存密钥按钮: Button = Button("保存").apply {
        text = "保存"
    }
    
    val 文件路径文本框: TextField = TextField().apply {
        prefWidth = 400.0
        isEditable = false
        promptText = "选择文件..."
    }
    
    val 选择文件按钮: Button = Button("选择文件").apply {
        text = "选择文件"
    }
    
    val 上传文件按钮: Button = Button("上传文件").apply {
        text = "上传文件"
    }
    
    val url文本框: TextField = TextField().apply {
        prefWidth = 400.0
        text = ""
    }
    
    val 转换按钮: Button = Button("转换").apply {
        text = "转换"
    }
    
    val 查询按钮: Button = Button("查询转换").apply {
        text = "查询转换"
    }
    
    val 获取结果按钮: Button = Button("获取结果").apply {
        text = "获取结果"
    }
    
    val 保存结果按钮: Button = Button("保存结果").apply {
        text = "保存结果"
    }
    
    val 从文件获取结果按钮: Button = Button("从文件获取结果").apply {
        text = "从文件获取结果"
    }
    
    val 结果列表: ListView<String> = ListView<String>().apply {
        prefHeight = 150.0
        prefWidth = 600.0
    }
    
    val 句子列表: ListView<String> = ListView<String>().apply {
        prefHeight = 200.0
        prefWidth = 600.0
    }
    
    val 转录片段表格: TableView<转录片段> = TableView<转录片段>().apply {
        prefHeight = 200.0
        prefWidth = 600.0
    }
    
    val 合成句子按钮: Button = Button("合成句子").apply {
        text = "合成句子"
    }
    
    val 分割句子按钮: Button = Button("分割句子").apply {
        text = "分割句子"
    }
    
    val 保存为字幕事件按钮: Button = Button("保存为字幕事件").apply {
        text = "保存为字幕事件"
    }
    
    fun 创建密钥框(): VBox {
        return VBox(
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
    }
    
    fun 创建文件选择框(): VBox {
        return VBox(
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
    }
    
    fun 创建转换框(): VBox {
        return VBox(
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
    }
    
    fun 创建查询框(): VBox {
        val 句子和转录片段框 = VBox(10.0).apply {
            children.addAll(
                VBox(Label("句子列表:"), 句子列表).apply { spacing = 10.0 },
                VBox(Label("转录片段表格:"), 转录片段表格).apply { spacing = 10.0 }
            )
        }
        
        val 句子操作按钮框 = HBox(10.0, 合成句子按钮, 分割句子按钮)
        
        return VBox(
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
    }
}