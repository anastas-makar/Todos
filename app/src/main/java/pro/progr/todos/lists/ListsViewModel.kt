package pro.progr.lists

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListsViewModel(private val repository: ListsRepository) : ViewModel() {
    val nestedListState : MutableState<List<NestedList>> = mutableStateOf(emptyList())
    val selectedListState : MutableState<NestedList?> = mutableStateOf(null)
    val sheetMode = mutableStateOf(SheetMode.HIDDEN)
    val modeState = mutableStateOf(Mode.SELECT)
    val editedListIdState = mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            repository.getListOfLists().collectLatest { listOfLists ->
                nestedListState.value = listOfLists

                if (listOfLists.isEmpty()) {
                    // ничего нет — сбрасываем выбор
                    selectedListState.value = null
                    return@collectLatest
                }

                val currentId = selectedListState.value?.id

                selectedListState.value = when {
                    currentId == null -> listOfLists.firstOrNull()
                    else -> listOfLists.firstOrNull { it.id == currentId } ?: listOfLists.firstOrNull()
                }
            }
        }

    }

    fun addSublistToList(list: NestedList) {
        viewModelScope.launch(Dispatchers.Default) {
            val resultId = repository.addSublistToList(list)
            withContext(Dispatchers.Main) {
                editedListIdState.value = resultId
            }
        }
    }

    fun findParent(list: NestedList) : NestedList? {
        return repository.findParent(list, nestedListState.value)

    }

    fun deleteList(list: NestedList) {
        viewModelScope.launch (Dispatchers.Default) {
            repository.deleteList(list)
        }
    }

    fun updateList(list: NestedList) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.updateList(list)

            withContext(Dispatchers.Main) {
                editedListIdState.value = null
            }
        }
    }

    /**
     * TODO: когда SublistChain будет перенесён в библиотеку lists,
     * нужно избавиться от путаницы и передавать SublistChain вместо listId : String
     */
    fun bindListToEntity(listId : String, entityId : String) {
        viewModelScope.launch (Dispatchers.Default) {
            repository.bindListToEntity(listId, entityId)
        }
    }

    enum class Mode {
        SELECT,
        ADD,
        EDIT
    }

    enum class SheetMode {
        SHOW,
        SHOWN,
        HIDE,
        HIDDEN
    }
}