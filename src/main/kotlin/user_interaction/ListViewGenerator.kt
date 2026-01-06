package org.wiamotit1e.user_interaction

import javafx.application.Platform
import javafx.beans.property.IntegerProperty
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback

class ListViewGenerator<T>(
    private val content: (T) -> String,
    private val style: (ListView<T>, T, Int) -> String = { _,_,_-> "" },
    private val onSelectionChanged: (T?) -> Unit = {},
    private val onSelectionIndexChanged: (Int) -> Unit = {},
) {
    private val listView: ListView<T> = ListView<T>()
    
    init {
        listView.cellFactory = Callback { _ ->
            object : ListCell<T>() {
                override fun updateItem(item: T?, empty: Boolean) {
                    super.updateItem(item, empty)
                    
                    if (item == null) {
                        Platform.runLater {
                            text = null
                        }
                        return
                    }
                    if (empty) {
                        Platform.runLater {
                            text = null
                        }
                    } else {
                        Platform.runLater {
                            text = content(item)
                            style = style(listView, item, index)
                        }
                    }
                }
            }
        }
        listView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            onSelectionChanged(newValue)
        }
        
        listView.selectionModel.selectedIndexProperty().addListener { _, _, newValue ->
            onSelectionIndexChanged(newValue.toInt())
        }
    }
    fun bindItems(observableList: ObservableList<T>): ListViewGenerator<T> {
        listView.items = observableList
        return this
    }
    
    fun bindSelectedItem(observableValue: ObjectProperty<T>): ListViewGenerator<T> {
        listView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            observableValue.set(newValue)
        }
        
        observableValue.addListener { _, _, newValue ->
            listView.selectionModel.select(newValue)
        }
        return this
    }
    
    fun bindSelectedIndex(observableValue: IntegerProperty): ListViewGenerator<T> {
        listView.selectionModel.selectedIndexProperty().addListener { _, _, newValue ->
            observableValue.set(newValue.toInt())
        }
        
        observableValue.addListener { _, _, newValue ->
            listView.selectionModel.select(newValue.toInt())
        }
        return this
    }
    
    fun get(): ListView<T> = listView
}