package org.wiamotit1e.user_interaction.old

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.util.Callback

class ListViewMaker<T>(
    val listView: ListView<T>,
    private val content: (T) -> String,
    private val style: (T) -> String = { "" },
    private val onSelectionChanged: (T?) -> Unit,
) {
    
    init {
        listView.cellFactory = Callback { _ ->
            object : ListCell<T>() {
                override fun updateItem(item: T?, empty: Boolean) {
                    super.updateItem(item, empty)
                    
                    if (item == null) {
                        text = null
                        return
                    }
                    if (empty) {
                        text = null
                    } else {
                        text = content(item)
                        style = style(item)
                    }
                }
            }
        }
        
        listView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            onSelectionChanged(newValue)
        }
    }
    
    val selectedIndex get() = listView.selectionModel.selectedIndex
    
    val selectedItem: T? get() = listView.selectionModel.selectedItem
    
    fun getItems(): ObservableList<T> {
        return listView.items
    }
    
    fun setItems(list: List<T>) {
        var var1 = FXCollections.observableArrayList<T>(list)
        listView.items.clear()
        listView.items.setAll(var1)
    }
}