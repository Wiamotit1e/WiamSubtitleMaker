package org.wiamotit1e

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.TableView
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.stage.Stage

class JavaFX应用 : Application() {
    
    override fun start(舞台: Stage) {
        // 创建UI组件管理器
        val ui组件管理器 = UI组件管理器()
        
        // 创建业务逻辑处理器
        val 业务处理器 = 业务逻辑处理器(ui组件管理器, 舞台)
        
        // 初始化所有事件处理器
        业务处理器.初始化事件处理器()
        
        // 创建主界面布局
        val 主界面 = VBox(
            ui组件管理器.创建密钥框(),
            ui组件管理器.创建文件选择框(),
            ui组件管理器.创建转换框(),
            ui组件管理器.创建查询框()
        ).apply {
            padding = javafx.geometry.Insets(20.0)
        }
        
        舞台.apply {
            title = "Wiam字幕制作器"
            scene = Scene(主界面, 800.0, 1000.0)
            show()
        }
    }
}