package pro.progr.todos

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pro.progr.todos.db.NoteListsDao
import pro.progr.todos.db.NotesDao
import pro.progr.todos.db.SublistChain
import pro.progr.lists.ListsRepository
import pro.progr.lists.NestedList
import javax.inject.Inject

class ListRepository @Inject constructor(
    private val noteListsDao: NoteListsDao,
    private val notesDao: NotesDao
) : ListsRepository {

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + parentJob)

    private val listsFlow : MutableStateFlow<List<NestedList>> = MutableStateFlow(emptyList())

    override fun getListOfLists(): Flow<List<NestedList>> {

        coroutineScope.launch {
            noteListsDao.getAllNoteLists().collectLatest { notesList ->

                if (notesList.isEmpty()) {
                    noteListsDao.insertFirst("Все записи", 1)
                } else {
                    listsFlow.value = notesList.map { noteList ->
                        ListsConverter.toNList(noteList)
                    }.sortedWith(noteListsComparator)
                }
            }

        }

        return listsFlow
    }

    override fun selectList(list: NestedList) {
    }

    override fun addSublistToList(list: NestedList) : Long {

        val nList = (list as NList)

        return noteListsDao.insertNextIntoSublist("", nList.sublistChain)
    }

    override fun deleteList(list: NestedList) {
        noteListsDao.deleteWithSubLists((list as NList).sublistChain)
    }

    override fun updateList(list: NestedList) {
        noteListsDao.update(ListsConverter.toNotesList(list as NList))
    }

    /**
     * TODO: когда SublistChain будет перенесён в библиотеку lists,
     * нужно избавиться от путаницы и передавать SublistChain вместо String
     */
    override fun bindListToEntity(listId: String, entityId: String) {
        notesDao.updateSublistChain(entityId, listId)
    }

    override fun findParent(list: NestedList, value: List<NestedList>): NestedList? {
        val parentChain = (list as NList).sublistChain.getParent()

        if (parentChain == null) {
            return null
        }

        return value.find {nestedList -> (nestedList as NList).sublistChain.equals(parentChain) }
    }

    private val noteListsComparator = Comparator<NList> { a, b ->
        return@Comparator SublistChain.sublistChainComparator.compare(a.sublistChain, b.sublistChain)
    }
}