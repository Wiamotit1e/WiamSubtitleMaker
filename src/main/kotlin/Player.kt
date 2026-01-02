package org.wiamotit1e

import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration.millis
import java.nio.file.Path


object Player {
    private var currentPlayer: MediaPlayer? = null
    
    fun play(pathToPlay: Path, startMilliseconds: Int, endMilliseconds: Int) {
        stop()
        try {
            val media = Media(pathToPlay.toUri().toString())
            val player = MediaPlayer(media)
            player.startTime = millis(startMilliseconds.toDouble())
            player.stopTime = millis(endMilliseconds.toDouble())
            player.play()
            currentPlayer = player
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    fun stop() {
        currentPlayer?.stop()
        currentPlayer = null
    }
}