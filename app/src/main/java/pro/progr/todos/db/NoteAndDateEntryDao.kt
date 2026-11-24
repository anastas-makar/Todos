package pro.progr.todos.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.room.Transaction
import pro.progr.diamondsandberries.db.FilterTypeConverter
import pro.progr.diamondsandberries.db.PatternDatesConverter
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.todos.datefilters.FilterType

@Dao
interface NoteAndDateEntryDao {
    @Update suspend fun update(note: Note): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDateEntry(entry: NoteDateEntry)

    @Query("SELECT * FROM note_date_entries WHERE noteId = :noteId AND date = :date")
    suspend fun getDateEntry(noteId: String, date: Long): NoteDateEntry?

    @Transaction
    @Query("SELECT * FROM note_date_entries WHERE noteId = :noteId AND date = :date LIMIT 1")
    suspend fun getDateEntryWithData(noteId: String, date: Long): NoteDateEntryWithData?

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteForScheduleUpdate(noteId: String): Note?

    @Query("UPDATE notes SET todo='DONE' WHERE id=:noteId AND pattern_type='NO_TERMS'")
    suspend fun updateUnscheduledNoteDone(noteId: String)

    @Query("UPDATE notes SET todo='TODO' WHERE id=:noteId AND pattern_type='NO_TERMS'")
    suspend fun updateUnscheduledNoteNotDone(noteId: String)

    @Query("UPDATE note_date_entries SET deleted = 1 WHERE date = :date AND noteId = :noteId")
    suspend fun markDateEntryDeleted(noteId: String, date: Long): Int

    @Query("UPDATE notes SET deleted = 1 WHERE id = :noteId")
    suspend fun softDeleteNote(noteId: String): Int

    @Query("UPDATE notes SET title = :title WHERE id = :noteId")
    suspend fun updateTitle(noteId: String, title: String)

    @Query("UPDATE notes SET description = :description WHERE id = :noteId")
    suspend fun updateDescription(noteId: String, description: String)

    @Query("UPDATE notes SET style = :style WHERE id = :noteId")
    @TypeConverters(ColorStyleConverter::class)
    suspend fun updateStyle(noteId: String, style: ColorStyle)

    @Query("UPDATE notes SET fillTitleBackground = :filled WHERE id = :noteId")
    suspend fun updateTitleBackground(noteId: String, filled: Boolean)

    @Query("UPDATE notes SET fillTextBackground = :filled WHERE id = :noteId")
    suspend fun updateTextBackground(noteId: String, filled: Boolean)

    @Query("UPDATE note_date_entries SET title = :title WHERE noteId = :noteId AND date = :date")
    suspend fun updateDateTitle(noteId: String, date: Long, title: String)

    @Query("UPDATE note_date_entries SET description = :description WHERE noteId = :noteId AND date = :date")
    suspend fun updateDateDescription(noteId: String, date: Long, description: String)

    @Query("UPDATE note_date_entries SET style = :style WHERE noteId = :noteId AND date = :date")
    @TypeConverters(ColorStyleConverter::class)
    suspend fun updateDateStyle(noteId: String, date: Long, style: ColorStyle)

    @Query("UPDATE note_date_entries SET fillTitleBackground = :filled WHERE noteId = :noteId AND date = :date")
    suspend fun updateDateTitleBackground(noteId: String, date: Long, filled: Boolean)

    @Query("UPDATE note_date_entries SET fillTextBackground = :filled WHERE noteId = :noteId AND date = :date")
    suspend fun updateDateTextBackground(noteId: String, date: Long, filled: Boolean)

    @Query("UPDATE note_date_entries SET todo = :todo WHERE noteId = :noteId AND date = :date")
    suspend fun updateDateTodo(noteId: String, date: Long, todo: TodoStatus)

    @Query("""UPDATE notes SET added_dates=:addedDates, cancelled_dates=:cancelledDates,
        pattern_days=:patternDays, pattern_type=:patternType, date_since=:dateSince, date_till=:dateTill
        WHERE id=:noteId""")
    @TypeConverters(LongArrayConverter::class, PatternDatesConverter::class, FilterTypeConverter::class)
    suspend fun updateScheduleColumns(
        noteId: String, addedDates: ArrayPOJO?, cancelledDates: ArrayPOJO?,
        patternDays: LinkedHashMap<Long, Schedule.Day>?, patternType: FilterType?,
        dateSince: Long?, dateTill: Long?
    ): Int

    @Query("""UPDATE notes SET added_dates=:addedDates, cancelled_dates=:cancelledDates,
        pattern_days=:patternDays, pattern_type='DATE', date_since=:epochDay, date_till=:epochDay
        WHERE id=:noteId AND coalesce(pattern_type, 'NO_TERMS')='NO_TERMS'""")
    @TypeConverters(LongArrayConverter::class, PatternDatesConverter::class)
    suspend fun scheduleUnscheduledNoteForDate(
        noteId: String,
        addedDates: ArrayPOJO?,
        cancelledDates: ArrayPOJO?,
        patternDays: LinkedHashMap<Long, Schedule.Day>?,
        epochDay: Long
    ): Int
}
