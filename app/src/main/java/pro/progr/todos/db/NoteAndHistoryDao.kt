package pro.progr.todos.db

import androidx.room.*
import pro.progr.todos.brightcards.colors.ColorStyle
import pro.progr.todos.brightcards.model.TodoStatus
import pro.progr.diamondsandberries.db.Schedule
import pro.progr.diamondsandberries.db.ScheduleConverter
import pro.progr.todos.NoteConverter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

@Dao
interface NoteAndHistoryDao {
    @Update
    suspend fun update(note: Note): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(noteInHistory: NoteInHistory)

    @Query("UPDATE notes_in_history SET todo = 'DONE', updated_at = :updatedAt " +
            "WHERE noteId = :noteId AND date = :day")
    fun setNoteInHistoryDone(noteId: String, day: Long,
                             updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) : Int


    @Query("UPDATE notes_in_history SET todo = 'TODO', " +
            " updated_at = :updatedAt WHERE noteId = :noteId AND date = :day")
    fun setNoteInHistoryNotDone(noteId: String, day: Long,
                                updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) : Int

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
        todo = :todo, 
        updated_at = :updatedAt 
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
        todo: TodoStatus,
        updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
    ): Int

    @Query("""
    UPDATE notes_in_history 
    SET title = :title, 
        description = :description,
        edited = 1, 
        updated_at = :updatedAt 
    WHERE noteId = :noteId AND date = :epochDay
""")
    fun editNoteInHistory(title: String, description: String, noteId: String, epochDay: Long,
                          updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))

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
            historyNote.updatedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            insert(noteInHistory = historyNote)
        }
    }

    @Query("SELECT COUNT(*) FROM notes_in_history WHERE noteId=:noteId AND date=:day AND edited=0 AND deleted = 0")
    suspend fun findNotEdited(noteId: String, day: Long): Int

    @Transaction
    suspend fun setCardDoneAndUpdateHistory(note: Note, historyNote: NoteInHistory, day: Long) {
        updateNoteDoneIfNoTerms(note.id)
        if (setNoteInHistoryDone(note.id, LocalDate.now().toEpochDay()) == 0) {
            historyNote.updatedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            insert(noteInHistory = historyNote)
        }
    }

    @Query("UPDATE notes SET todo='DONE', updated_at = :updatedAt WHERE id=:noteId AND pattern_type='NO_TERMS'")
    suspend fun updateNoteDoneIfNoTerms(noteId: String,
                                        updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))

    @Transaction
    suspend fun setCardNotDoneAndUpdateHistory(note: Note, historyNote: NoteInHistory, day: Long) {
        if (setNoteInHistoryNotDone(note.id, LocalDate.now().toEpochDay()) == 0) {
            historyNote.updatedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
            insert(noteInHistory = historyNote)
        }
    }

    @Transaction
    suspend fun setCardDone(note: Note, day: Long) {
        setNoteInHistoryDone(note.id, day)
    }

    @Transaction
    suspend fun updateNoteDates(note: Note) {
        note.updatedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
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

    @Query("UPDATE notes_in_history SET deleted = 1, " +
            "updated_at = :updatedAt WHERE date=:date AND noteId=:noteId")
    fun remoCardForDay(date: Long, noteId: String,
                       updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))

    @Query("SELECT MAX(date) FROM notes_in_history WHERE deleted = 0")
    fun getLatestDate(): Long?

    @Query("SELECT * FROM notes_in_history WHERE noteId=:noteId AND date=:epochDay AND deleted = 0")
    fun getNoteInHistory(noteId: String, epochDay: Long): NoteInHistory?

    /**
     * Для других дней история может сохраняться при удалении задачи, а на сегодня -- нет
     */
    @Query("UPDATE notes_in_history SET deleted = 1, updated_at = :updatedAt WHERE date = :today AND noteId IN(" +
            " SELECT id FROM notes WHERE coalesce(title, '') = '' AND coalesce(description, '') = ''" +
            ")")
    fun deleteUnlinkedHistoryForToday(today : Long,
                                      updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)) : Int

    @Query("UPDATE notes SET deleted = 1, updated_at = :updatedAt WHERE coalesce(title, '') = '' AND coalesce(description, '') = ''")
    fun deleteEmptyNotes(updatedAt : Long = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)): Int

    @Transaction
    fun deleteEmptyNotesAndUnlinkedHistoryForToday(today: Long) : Int {
        deleteUnlinkedHistoryForToday(today)
        return deleteEmptyNotes()
    }
}