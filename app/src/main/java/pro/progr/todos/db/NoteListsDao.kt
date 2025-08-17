package pro.progr.todos.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.collections.List

@Dao
interface NoteListsDao {

    @Query("INSERT INTO note_lists(title, is_current, updated_at, sublist_chain, id) SElECT :listName, 0, :updatedAt, " +
            ":parentChain || ':' || (MAX(REPLACE(sublist_chain, :parentChain || ':', \"\")) + 1), :id " +
            " FROM note_lists WHERE sublist_chain LIKE :parentChain || '%'" +
            " AND NOT sublist_chain LIKE :parentChain || '%:'")
    fun insertNextIntoSublist(listName : String,
                              @TypeConverters(SublistChainConverter::class) parentChain : SublistChain,
                              id: String,
                              updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))

    @Query("INSERT INTO note_lists(title, is_current, sublist_chain, updated_at, id) VALUES (:listName, :current, 1, :updatedAt, :id)")
    fun insertFirst(listName: String, current : Int, id:String,
                    updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
/*
    Использовать метод insertNextIntoSublist
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(notesLists: List<NotesList>) : List<Long>*/

    @Update
    fun update(notesList: NotesList) : Int

    @Query("SELECT * FROM note_lists WHERE deleted = 0")
    fun getAllNoteLists() : Flow<List<NotesList>>

    @Query("UPDATE note_lists SET deleted = 1, updated_at = :updatedAt WHERE sublist_chain LIKE :sublistChain || '%'")
    fun deleteWithSubLists(@TypeConverters(SublistChainConverter::class) sublistChain: SublistChain,
                           updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
}