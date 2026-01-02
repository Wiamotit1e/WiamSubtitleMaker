package org.wiamotit1e

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.*
import javafx.stage.Stage

class JavaFXApplication : Application() {
    
    override fun start(currentStage: Stage) {
        // 创建UI组件管理器
        val uiComponentManager = UIComponentManager()
        
        // 创建业务逻辑处理器
        val businessLogicProcessor = BusinessLogicProcessor(uiComponentManager, currentStage)
        
        // 初始化所有事件处理器
        businessLogicProcessor.initializeEventManager()
        
        // 创建主界面布局
        val mainWindow = VBox(
            uiComponentManager.createApiKeyBox(),
            uiComponentManager.createFileSelectionBox(),
            uiComponentManager.createTranscribeBox(),
            uiComponentManager.createSearchBox()
        ).apply {
            padding = javafx.geometry.Insets(20.0)
        }
        
        currentStage.apply {
            title = "Wiam字幕制作器"
            scene = Scene(mainWindow, 800.0, 1000.0)
            show()
        }
    }
}