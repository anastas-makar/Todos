package pro.progr.todos.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlin.collections.List

@Dao
interface NoteListsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notesList: NotesList) : Long

    @Query("INSERT INTO note_lists(title, is_current, sublist_chain) SElECT :listName, 0, " +
            ":parentChain || ':' || (MAX(REPLACE(sublist_chain, :parentChain || ':', \"\")) + 1) FROM note_lists WHERE sublist_chain LIKE :parentChain || '%'" +
            " AND NOT sublist_chain LIKE :parentChain || '%:'")
    fun insertNextIntoSublist(listName : String,
                              @TypeConverters(SublistChainConverter::class) parentChain : SublistChain) : Long

    @Query("INSERT INTO note_lists(title, is_current, sublist_chain) VALUES (:listName, :current, 1)")
    fun insertFirst(listName: String, current : Int)
/*
    Использовать метод insertNextIntoSublist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notesLists: List<NotesList>) : List<Long>*/

    @Update
    fun update(notesList: NotesList) : Int

    @Query("UPDATE note_lists SET is_current = CASE WHEN id = :id THEN 1 ELSE 0 END")
    fun setNewCurrent(id: Int)

    @Query("SELECT * FROM note_lists")
    fun getAllNoteLists() : Flow<List<NotesList>>

    @Query("SELECT * FROM note_lists WHERE id = :id")
    fun getNoteListById(id : Int) : Flow<List<NotesList>>

    @Delete
    fun delete(list: NotesList)

    @Query("DELETE FROM note_lists WHERE sublist_chain LIKE :sublistChain || '%'")
    fun deleteWithSubLists(@TypeConverters(SublistChainConverter::class) sublistChain: SublistChain)
}