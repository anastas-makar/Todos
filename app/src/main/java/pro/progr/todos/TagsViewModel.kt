package pro.progr.todos

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import pro.progr.todos.db.NoteTag
import javax.inject.Inject

class TagsViewModel @Inject constructor(private val tagsRepository: TagsRepository) : ViewModel() {
    var showTagsSheet = mutableStateOf(false)

    fun getAllTags() : Flow<List<NoteTag>?> {
        return tagsRepository.tagsFlow
    }

    fun insert(tagName: String) {
        viewModelScope.launch (Dispatchers.Default) {
            tagsRepository.insert(
                NoteTag(
                    title = tagName
                )
            )
        }
    }
}