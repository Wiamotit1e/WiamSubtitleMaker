package org.wiamotit1e

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration.millis
import java.nio.file.Path


object 播放器 {
    private var currentPlayer: MediaPlayer? = null
    
    fun play(路径: Path, 开始毫秒数: Int, 结束毫秒数: Int) {
        stop()
        try {
            val 媒体 = Media(路径.toUri().toString())
            val 播放器 = MediaPlayer(媒体)
            播放器.startTime = millis(开始毫秒数.toDouble())
            播放器.stopTime = millis(结束毫秒数.toDouble())
            播放器.play()
            currentPlayer = 播放器
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun stop() {
        currentPlayer?.stop()
        currentPlayer = null
    }
}