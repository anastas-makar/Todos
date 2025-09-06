package pro.progr.todos.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlin.collections.List

@Dao
interface NoteListsDao {

    @Query("SELECT * FROM note_lists WHERE id IN (:uuids)")
    suspend fun getUpdates(uuids: List<String>) : List<NotesList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setUpdates(updates : List<NotesList>)

    @Query("INSERT INTO note_lists(title, is_current, sublist_chain, id) SElECT :listName, 0, " +
            ":parentChain || ':' || (MAX(REPLACE(sublist_chain, :parentChain || ':', \"\")) + 1), :id " +
            " FROM note_lists WHERE sublist_chain LIKE :parentChain || '%'" +
            " AND NOT sublist_chain LIKE :parentChain || '%:'")
    fun insertNextIntoSublist(listName : String,
                              @TypeConverters(SublistChainConverter::class) parentChain : SublistChain,
                              id: String)

    @Query("INSERT INTO note_lists(title, is_current, sublist_chain, id) VALUES (:listName, :current, 1, :id)")
    fun insertFirst(listName: String, current : Int, id:String)
/*
    Использовать метод insertNextIntoSublist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notesLists: List<NotesList>) : List<Long>*/

    @Update
    fun update(notesList: NotesList) : Int

    @Query("SELECT * FROM note_lists WHERE deleted = 0")
    fun getAllNoteLists() : Flow<List<NotesList>>

    @Query("UPDATE note_lists SET deleted = 1 WHERE sublist_chain LIKE :sublistChain || '%'")
    fun deleteWithSubLists(@TypeConverters(SublistChainConverter::class) sublistChain: SublistChain)
}