package pro.progr.todos

import kotlinx.coroutines.flow.Flow
import pro.progr.todos.db.NoteTag
import pro.progr.todos.db.TagsDao
import javax.inject.Inject

class TagsRepository @Inject constructor(val tagsDao: TagsDao) {
    private val _tagsFlow : Flow<List<NoteTag>?>
    val tagsFlow get() = _tagsFlow

    init {
        _tagsFlow = getAll()
    }
    private fun getAll() : Flow<List<NoteTag>?> {
        return tagsDao.getAllTags()
    }

    fun insert(tag: NoteTag) {
        tagsDao.insert(tag)
    }
}