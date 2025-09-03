package pro.progr.todos.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
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
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            db.execSQL("""
                CREATE TABLE IF NOT EXISTS outbox(
                  id INTEGER PRIMARY KEY AUTOINCREMENT,
                  table_name TEXT NOT NULL,
                  row_id TEXT NOT NULL,
                  op TEXT NOT NULL,
                  created_at INTEGER NOT NULL
                );
            """.trimIndent())

                            // Набор триггеров для всех нужных таблиц:
                            db.execSQL("""
                CREATE TRIGGER IF NOT EXISTS trg_notes_ins_outbox
                AFTER INSERT ON notes BEGIN
                  INSERT INTO outbox(table_name,row_id,op,created_at)
                  VALUES('notes', NEW.id, 'UPSERT', CAST(strftime('%s','now') AS INTEGER)*1000);
                END;
            """.trimIndent())
                            db.execSQL("""
                CREATE TRIGGER IF NOT EXISTS trg_notes_upd_outbox
                AFTER UPDATE ON notes BEGIN
                  INSERT INTO outbox(table_name,row_id,op,created_at)
                  VALUES('notes', NEW.id, 'UPSERT', CAST(strftime('%s','now') AS INTEGER)*1000);
                END;
            """.trimIndent())
                            // todo: для tags, note_to_tag_xref, и т.д.
                        }
                    })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}