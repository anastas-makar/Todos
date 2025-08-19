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
abstract class TodosDataBase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
    abstract fun noteListsDao(): NoteListsDao
    abstract fun noteWithDataDao(): NoteWithDataDao
    abstract fun tagsDao(): TagsDao
    abstract fun noteAndHistoryDao(): NoteAndHistoryDao
    abstract fun notesInHistoryDao(): NotesInHistoryDao
    abstract fun diamondsCountDao(): DiamondsCountDao

    abstract fun noteToTagXRefDao(): NoteToTagXRefDao

    companion object {
        @Volatile
        private var INSTANCE: TodosDataBase? = null

        fun getDatabase(context: Context): TodosDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodosDataBase::class.java,
                    "todos_database"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_1_3, MIGRATION_1_4, MIGRATION_2_4, MIGRATION_3_4)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}