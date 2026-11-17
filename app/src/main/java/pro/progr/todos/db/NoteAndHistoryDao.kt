package pro.progr.todos.db

import androidx.room.*
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.NoteConverter
import pro.progr.todos.datefilters.FilterType
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.diamondsandberries.db.FilterTypeConverter
import pro.progr.diamondsandberries.db.PatternDatesConverter
import java.time.LocalDate
import java.util.LinkedHashMap

@Dao
interface NoteAndHistoryDao {
    @Update
    suspend fun update(note: Note): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteInHistory: NoteInHistory)

    @Query("UPDATE notes_in_history SET todo = 'DONE' " +
            "WHERE noteId = :noteId AND date = :day")
    fun setNoteInHistoryDone(noteId: String, day: Long) : Int


    @Query("UPDATE notes_in_history SET todo = 'TODO' " +
            "  WHERE noteId = :noteId AND date = :day")
    fun setNoteInHistoryNotDone(noteId: String, day: Long) : Int

    //todo: нужно заменить это на update, т.к. при изменении карточки она и на сегодня должна меняться
    // Completion status belongs to the dated history entry; template edits must not reset it.
    @Query("""
    UPDATE notes_in_history 
    SET title = :title, 
        description = :description, 
        reward = :reward, 
        sublist_chain = :sublistChain, 
        style = :style, 
        fillTitleBackground = :fillTitleBackground, 
        fillTextBackground = :fillTextBackground 
    WHERE noteId = :noteId AND date = :date
""")
    @TypeConverters(SublistChainConverter::class)
    fun updateNoteInHistory(
        noteId: String,
        date: Long,
        title: String,
        description: String,
        reward: Int,
        sublistChain: SublistChain,
        style: ColorStyle,
        fillTitleBackground: Boolean,
        fillTextBackground: Boolean
    ): Int

    @Query("""
    UPDATE notes_in_history
    SET title = :title,
        description = :description,
        reward = :reward,
        sublist_chain = :sublistChain,
        style = :style,
        fillTitleBackground = :fillTitleBackground,
        fillTextBackground = :fillTextBackground,
        todo = :todo
    WHERE noteId = :noteId AND date = :date
    """)
    @TypeConverters(SublistChainConverter::class)
    fun updateNewCardInHistory(
        noteId: String,
        date: Long,
        title: String,
        description: String,
        reward: Int,
        sublistChain: SublistChain,
        style: ColorStyle,
        fillTitleBackground: Boolean,
        fillTextBackground: Boolean,
        todo: pro.progr.todos.brightcards.model.TodoStatus
    ): Int

    @Query("""
    UPDATE notes_in_history 
    SET title = :title, 
        description = :description,
        edited = 1 
    WHERE noteId = :noteId AND date = :epochDay
""")
    fun editNoteInHistory(title: String, description: String, noteId: String, epochDay: Long)

    @Query("UPDATE notes_in_history SET fillTitleBackground = :filled, edited = 1 WHERE noteId = :noteId AND date = :epochDay")
    fun updateHistoryTitleBackground(noteId: String, epochDay: Long, filled: Boolean): Int

    @Query("UPDATE notes_in_history SET fillTextBackground = :filled, edited = 1 WHERE noteId = :noteId AND date = :epochDay")
    fun updateHistoryDescriptionBackground(noteId: String, epochDay: Long, filled: Boolean): Int

    @TypeConverters(ColorStyleConverter::class)
    @Query("UPDATE notes_in_history SET style = :style, edited = 1 WHERE noteId = :noteId AND date = :epochDay")
    fun updateHistoryStyle(noteId: String, epochDay: Long, style: ColorStyle): Int

    @Transaction
    suspend fun updateDaysNoteHistoryIfNotEdited(historyNote: NoteInHistory, day: Long) {
        val existing = getHistoryEntryIncludingDeleted(historyNote.noteId, day)
        if (existing?.edited == true || existing?.deleted == true) return
        if (existing == null) {
            insert(historyNote)
        } else {
            // A template edit must preserve the completion state of this dated entry.
            updateNoteInHistory(
                historyNote.noteId, day, historyNote.title, historyNote.description,
                historyNote.reward, historyNote.sublistChain, historyNote.style,
                historyNote.fillTitleBackground, historyNote.fillTextBackground
            )
        }
    }

    @Transaction
    suspend fun updateNewCardHistoryIfNotEdited(historyNote: NoteInHistory, day: Long) {
        val existing = getHistoryEntryIncludingDeleted(historyNote.noteId, day)
        if (existing?.edited == true || existing?.deleted == true) return
        if (existing == null) {
            insert(historyNote)
        } else {
            // During initial creation, the dated entry must receive the chosen card kind/status.
            updateNewCardInHistory(
                historyNote.noteId, day, historyNote.title, historyNote.description,
                historyNote.reward, historyNote.sublistChain, historyNote.style,
                historyNote.fillTitleBackground, historyNote.fillTextBackground, historyNote.todo
            )
        }
    }

    @Query("SELECT COUNT(*) FROM notes_in_history WHERE noteId=:noteId AND date=:day AND edited=0 AND deleted = 0")
    suspend fun findNotEdited(noteId: String, day: Long): Int

    @Query("SELECT * FROM notes_in_history WHERE noteId = :noteId AND date = :day")
    suspend fun getHistoryEntryIncludingDeleted(noteId: String, day: Long): NoteInHistory?

    @Transaction
    suspend fun setCardDoneAndUpdateHistory(note: Note, historyNote: NoteInHistory, day: Long) {
        updateNoteDoneIfNoTerms(note.id)
        if (setNoteInHistoryDone(note.id, LocalDate.now().toEpochDay()) == 0) {
            insert(noteInHistory = historyNote)
        }
    }

    @Query("UPDATE notes SET todo='DONE' WHERE id=:noteId AND pattern_type='NO_TERMS'")
    suspend fun updateNoteDoneIfNoTerms(noteId: String)

    @Transaction
    suspend fun setCardNotDoneAndUpdateHistory(note: Note, historyNote: NoteInHistory, day: Long) {
        if (setNoteInHistoryNotDone(note.id, LocalDate.now().toEpochDay()) == 0) {
            insert(noteInHistory = historyNote)
        }
    }

    @Transaction
    suspend fun setCardDone(note: Note, day: Long) {
        setNoteInHistoryDone(note.id, day)
    }

    @Transaction
    suspend fun updateNoteDates(note: Note) {
        update(note)

        val date = LocalDate.now().toEpochDay()
        val noteInHistory = getNoteInHistory(noteId = note.id, date)

        val schedule = note.toSchedule()
        val filterType = note.patternType ?: FilterType.NO_TERMS
        if (!filterType.datesFilter.isActual(schedule, LocalDate.now())
            && noteInHistory != null
        ) {
            remoCardForDay(date, note.id)
        }

        if (filterType.datesFilter.isActual(schedule, LocalDate.now())
            && noteInHistory == null) {
            insert(NoteConverter.toNoteInHistory(note, date))
        }

    }

    @Query("UPDATE notes_in_history SET deleted = 1 " +
            " WHERE date=:date AND noteId=:noteId")
    fun remoCardForDay(date: Long, noteId: String)

    @Query("SELECT MAX(date) FROM notes_in_history WHERE deleted = 0")
    fun getLatestDate(): Long?

    @Query("SELECT * FROM notes_in_history WHERE noteId=:noteId AND date=:epochDay AND deleted = 0")
    fun getNoteInHistory(noteId: String, epochDay: Long): NoteInHistory?

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteForScheduleUpdate(noteId: String): Note?

    @Query("""
        UPDATE notes SET
            added_dates = :addedDates,
            cancelled_dates = :cancelledDates,
            pattern_days = :patternDays,
            pattern_type = :patternType,
            date_since = :dateSince,
            date_till = :dateTill
        WHERE id = :noteId
    """)
    @TypeConverters(
        LongArrayConverter::class,
        PatternDatesConverter::class,
        FilterTypeConverter::class
    )
    suspend fun updateScheduleColumns(
        noteId: String,
        addedDates: ArrayPOJO?,
        cancelledDates: ArrayPOJO?,
        patternDays: LinkedHashMap<Long, Schedule.Day>?,
        patternType: FilterType?,
        dateSince: Long?,
        dateTill: Long?
    ): Int

    /** Update only scheduling columns, then reconcile today's history from a fresh DB row. */
    @Transaction
    suspend fun updateScheduleAndHistory(noteId: String, schedule: Schedule) {
        val note = getNoteForScheduleUpdate(noteId) ?: return
        note.applySchedule(schedule)
        if (updateScheduleColumns(
                note.id,
                note.addedDates,
                note.cancelledDates,
                note.patterDates,
                note.patternType,
                note.dateSince,
                note.dateTill
            ) == 0
        ) return

        val today = LocalDate.now()
        val day = today.toEpochDay()
        val history = getNoteInHistory(noteId, day)
        val isScheduledToday = (note.patternType ?: FilterType.NO_TERMS)
            .datesFilter.isActual(note.toSchedule(), today)

        if (!isScheduledToday && history != null) {
            remoCardForDay(day, noteId)
        } else if (isScheduledToday && history == null) {
            insert(NoteConverter.toNoteInHistory(note, day))
        }
    }

    /**
     * Для других дней история может сохраняться при удалении задачи, а на сегодня -- нет
     */
    @Query("UPDATE notes_in_history SET deleted = 1 WHERE date = :today AND noteId IN(" +
            " SELECT id FROM notes WHERE coalesce(title, '') = '' AND coalesce(description, '') = ''" +
            ")")
    fun deleteUnlinkedHistoryForToday(today : Long) : Int

    @Query("UPDATE notes SET deleted = 1 WHERE coalesce(title, '') = '' AND coalesce(description, '') = ''")
    fun deleteEmptyNotes(): Int

    @Transaction
    fun deleteEmptyNotesAndUnlinkedHistoryForToday(today: Long) : Int {
        deleteUnlinkedHistoryForToday(today)
        return deleteEmptyNotes()
    }
}
