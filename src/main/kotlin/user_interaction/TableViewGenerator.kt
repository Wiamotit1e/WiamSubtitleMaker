package org.wiamotit1e.user_interaction

import javafx.application.Platform
import javafx.beans.property.IntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.ObservableList
import javafx.scene.control.ListCell
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.util.Callback

class TableViewGenerator<T>(
    tableColumnConfigs: List<TableColumnConfig<T, Any>>,
    private val onSelectionChanged: (T?) -> Unit = {},
) {
    private val tableView: TableView<T> = TableView<T>()
    
    init {
        tableColumnConfigs.forEach { configs ->
            tableView.columns.add(
                TableColumn<T, Any>(configs.title).apply {
                    cellValueFactory = Callback { cellData -> SimpleObjectProperty(configs.value(cellData.value)) }
                    cellFactory = Callback { _ ->
                        object : TableCell<T, Any>() {
                            override fun updateItem(item: Any?, empty: Boolean) {
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
                                        text = configs.content(item)
                                        style = configs.style(this@apply, item, index)
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }
        tableView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            onSelectionChanged(newValue)
        }
    }
    
    fun bindItems(observableList: ObservableList<T>): TableViewGenerator<T> {
        tableView.items = observableList
        return this
    }
    
    fun bindSelectedItem(observableValue: SimpleObjectProperty<T>): TableViewGenerator<T> {
        tableView.selectionModel.selectedItemProperty().addListener { _, _, newValue ->
            observableValue.set(newValue)
        }
        
        observableValue.addListener { _, _, newValue ->
            tableView.selectionModel.select(newValue)
        }
        return this
    }
    
    fun bindSelectedIndex(observableValue: IntegerProperty): TableViewGenerator<T> {
        tableView.selectionModel.selectedIndexProperty().addListener { _, _, newValue ->
            observableValue.set(newValue.toInt())
        }
        
        observableValue.addListener { _, _, newValue ->
            tableView.selectionModel.select(newValue.toInt())
        }
        return this
    }
    
    fun get(): TableView<T> = tableView
}

class TableColumnConfig<T, S>(
    val title: String,
    val value: (T) -> S,
    val content: (S) -> String,
    val style: (TableColumn<T, S>, S, Int) -> String = { _, _, _-> "" },
)

