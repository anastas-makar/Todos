package pro.progr.todos.db

import androidx.room.*
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.diamondsandberries.db.ScheduleConverter
import pro.progr.todos.NoteConverter
import java.time.LocalDate

@Dao
interface NoteAndHistoryDao {
    @Update
    suspend fun update(note: Note): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteInHistory: NoteInHistory): Long

    @Query("INSERT INTO diamonds_count (day, count) VALUES(:day, :count)")
    fun insertDiamondsCount(day: Long, count : Int)

    @Query("UPDATE diamonds_count SET count = (count + :count) WHERE day = :day")
    fun updateDiamondsCount(day: Long, count : Int) : Int


    @Query("UPDATE notes_in_history SET todo = 'DONE' WHERE noteId = :noteId AND date = :day")
    fun setNoteInHistoryDone(noteId: String, day: Long) : Int


    @Query("UPDATE notes_in_history SET todo = 'TODO' WHERE noteId = :noteId AND date = :day")
    fun setNoteInHistoryNotDone(noteId: String, day: Long) : Int

    //todo: нужно заменить это на update, т.к. при изменении карточки она и на сегодня должна меняться
    @Query("""
    UPDATE notes_in_history 
    SET title = :title, 
        description = :description, 
        reward = :reward, 
        sublist_chain = :sublistChain, 
        schedule = :schedule, 
        style = :style, 
        fillTitleBackground = :fillTitleBackground, 
        fillTextBackground = :fillTextBackground, 
        todo = :todo 
    WHERE noteId = :noteId AND date = :date
""")
    @TypeConverters(ScheduleConverter::class, SublistChainConverter::class)
    fun updateNoteInHistory(
        noteId: String,
        date: Long,
        title: String,
        description: String,
        reward: Int,
        sublistChain: SublistChain,
        schedule: Schedule,
        style: ColorStyle,
        fillTitleBackground: Boolean,
        fillTextBackground: Boolean,
        todo: TodoStatus
    ): Int

    @Query("""
    UPDATE notes_in_history 
    SET title = :title, 
        description = :description,
        edited = 1
    WHERE noteId = :noteId AND date = :epochDay
""")
    fun editNoteInHistory(title: String, description: String, noteId: Long, epochDay: Long)

    @Transaction
    suspend fun updateDaysNoteHistoryIfNotEdited(historyNote: NoteInHistory, day: Long) {
        //Если задача в истории уже редактировалась, не меняем её
        if (findNotEdited(historyNote.noteId, day) == 0) {
            return
        }

        if (updateNoteInHistory(noteId = historyNote.noteId,
                date = day,
                title = historyNote.title,
                description = historyNote.description,
                reward = historyNote.reward,
                sublistChain = historyNote.sublistChain,
                schedule = historyNote.schedule,
                style = historyNote.style,
                fillTitleBackground = historyNote.fillTitleBackground,
                fillTextBackground = historyNote.fillTextBackground,
                todo = historyNote.todo
            ) == 0) {
            insert(noteInHistory = historyNote)
        }
    }

    @Query("SELECT COUNT(*) FROM notes_in_history WHERE noteId=:noteId AND date=:day AND edited=0")
    suspend fun findNotEdited(noteId: String, day: Long): Int

    @Transaction
    suspend fun setCardDoneAndUpdateHistory(note: Note, historyNote: NoteInHistory, day: Long) {
        updateNoteDoneIfNoTerms(note.id)
        if (setNoteInHistoryDone(note.id, LocalDate.now().toEpochDay()) == 0) {
            insert(noteInHistory = historyNote)
        }

        if ( updateDiamondsCount(day, note.reward) == 0) {
            insertDiamondsCount(day, note.reward)
        }
    }

    @Query("UPDATE notes SET todo='DONE' WHERE id=:noteId AND pattern_type='NO_TERMS'")
    suspend fun updateNoteDoneIfNoTerms(noteId: String)

    @Transaction
    suspend fun setCardNotDoneAndUpdateHistory(note: Note, historyNote: NoteInHistory, day: Long) {
        if (setNoteInHistoryNotDone(note.id, LocalDate.now().toEpochDay()) == 0) {
            insert(noteInHistory = historyNote)
        }

        updateDiamondsCount(day, -note.reward)
    }

    @Transaction
    suspend fun setCardDoneAndUpdateCount(note: Note, day: Long) {
        setNoteInHistoryDone(note.id, day)

        if (updateDiamondsCount(day, note.reward) == 0) {
            insertDiamondsCount(day, note.reward)
        }
    }

    @Transaction
    suspend fun updateNoteDates(note: Note) {
        update(note)

        val date = LocalDate.now().toEpochDay()
        val noteInHistory = getNoteInHistory(noteId = note.id, date)

        if (!note.schedule.pattern.type.datesFilter.isActual(note.schedule, LocalDate.now())
            && noteInHistory != null
        ) {
            remoCardForDay(date, note.id)
        }

        if (note.schedule.pattern.type.datesFilter.isActual(note.schedule, LocalDate.now())
            && noteInHistory == null) {
            insert(NoteConverter.toNoteInHistory(note, date))
        }

    }

    @Query("DELETE FROM notes_in_history WHERE date=:date AND noteId=:noteId")
    fun remoCardForDay(date: Long, noteId: String)

    @Query("SELECT MAX(date) FROM notes_in_history")
    fun getLatestDate(): Long?

    @Query("SELECT * FROM notes_in_history WHERE noteId=:noteId AND date=:epochDay")
    fun getNoteInHistory(noteId: String, epochDay: Long): NoteInHistory?

    /**
     * Для других дней история может сохраняться при удалении задачи, а на сегодня -- нет
     */
    @Query("DELETE FROM notes_in_history WHERE date = :today AND noteId IN(" +
            " SELECT id FROM notes WHERE coalesce(title, '') = '' AND coalesce(description, '') = ''" +
            ")")
    fun deleteUnlinkedHistoryForToday(today : Long) : Int

    @Query("DELETE FROM notes WHERE coalesce(title, '') = '' AND coalesce(description, '') = ''")
    fun deleteEmptyNotes(): Int

    @Transaction
    fun deleteEmptyNotesAndUnlinkedHistoryForToday(today: Long) : Int {
        deleteUnlinkedHistoryForToday(today)
        return deleteEmptyNotes()
    }
}