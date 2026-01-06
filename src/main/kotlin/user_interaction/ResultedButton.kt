package org.wiamotit1e.user_interaction

import javafx.scene.control.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ResultedButton(
    var1: String,
    val onResult: DoForMessage
): Button(var1) {
    fun setResultedOnAction(action: () -> Message) {
        setOnAction {
            onResult.doForMessage(action())
        }
    }
    
    fun setResultedOnSuspendAction(suspendAction: suspend () -> Message) {
        setOnAction {
            CoroutineScope(Dispatchers.IO).launch {
                val result = suspendAction()
                    onResult.doForMessage(result)
            }
        }
    }
}