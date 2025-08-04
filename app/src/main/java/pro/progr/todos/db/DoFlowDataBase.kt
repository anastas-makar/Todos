package pro.progr.todos.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pro.progr.diamondsandberries.db.FilterTypeConverter
import pro.progr.diamondsandberries.db.PatternDatesConverter
import pro.progr.diamondsandberries.db.ScheduleDatesConverter

@Database(
    entities = [
        Note::class,
        NoteInHistory::class,
        NotesList::class,
        NoteTag::class,
        NoteToTagXRef::class,
        DiamondsCount::class], version = 4
)
@TypeConverters(
    SublistChainConverter::class,
    ColorStyleConverter::class,
    LongArrayConverter::class,
    ScheduleDatesConverter::class,
    PatternDatesConverter::class,
    FilterTypeConverter::class
)
abstract class DoFlowDataBase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
    abstract fun noteListsDao(): NoteListsDao
    abstract fun noteWithDataDao(): NoteWithDataDao
    abstract fun tagsDao(): TagsDao
    abstract fun noteAndHistoryDao(): NoteAndHistoryDao
    abstract fun notesInHistoryDao(): NotesInHistoryDao
    abstract fun diamondsCountDao(): DiamondsCountDao

    companion object {
        @Volatile
        private var INSTANCE: DoFlowDataBase? = null

        fun getDatabase(context: Context): DoFlowDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DoFlowDataBase::class.java,
                    "todos_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}