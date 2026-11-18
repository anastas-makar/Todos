package pro.progr.todos.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import pro.progr.diamondsandberries.db.FilterTypeConverter
import pro.progr.diamondsandberries.db.PatternDatesConverter
//import pro.progr.todos.util.DeviceIdProvider
import java.util.UUID

@Database(
    entities = [
        Note::class,
        NoteDateEntry::class,
        NotesList::class,
        NoteTag::class,
        NoteToTagXRef::class,
        DiamondsCount::class,
        DiamondsLog::class,
        Outbox::class,
        AppMeta::class], version = 6
)
@TypeConverters(
    SublistChainConverter::class,
    ColorStyleConverter::class,
    LongArrayConverter::class,
    PatternDatesConverter::class,
    FilterTypeConverter::class
)
abstract class TodosDataBase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
    abstract fun noteListsDao(): NoteListsDao
    abstract fun noteWithDataDao(): NoteWithDataDao
    abstract fun tagsDao(): TagsDao
    abstract fun noteAndDateEntryDao(): NoteAndDateEntryDao
    abstract fun noteDateEntriesDao(): NoteDateEntriesDao
    abstract fun diamondsCountDao(): DiamondsCountDao

    abstract fun noteToTagXRefDao(): NoteToTagXRefDao

    abstract fun outBoxDao(): OutboxDao

    abstract fun diamondsLogDao(): DiamondsLogDao

    abstract fun appMetDao(): AppMetaDao

    companion object {
        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS note_date_entries (
                        id TEXT NOT NULL,
                        noteId TEXT NOT NULL,
                        date INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        description TEXT NOT NULL,
                        style TEXT NOT NULL,
                        fillTitleBackground INTEGER NOT NULL,
                        fillTextBackground INTEGER NOT NULL,
                        todo TEXT NOT NULL,
                        deleted INTEGER NOT NULL DEFAULT 0,
                        PRIMARY KEY(id)
                    )
                """.trimIndent())
                db.execSQL("""
                    INSERT OR REPLACE INTO note_date_entries
                    (id, noteId, date, title, description, style, fillTitleBackground,
                     fillTextBackground, todo, deleted)
                    SELECT id, noteId, date, title, description, style, fillTitleBackground,
                           fillTextBackground, todo, deleted
                    FROM notes_in_history
                    WHERE edited = 1 OR todo = 'DONE' OR deleted = 1
                """.trimIndent())
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_note_date_entries_noteId_date ON note_date_entries(noteId, date)")
                db.execSQL("UPDATE outbox SET table_name='note_date_entries' WHERE table_name='notes_in_history' AND row_id IN (SELECT id FROM note_date_entries)")
                db.execSQL("DELETE FROM outbox WHERE table_name='notes_in_history'")
                db.execSQL("DROP TABLE notes_in_history")
            }
        }

        @Volatile
        private var INSTANCE: TodosDataBase? = null

        fun getDatabase(context: Context): TodosDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodosDataBase::class.java,
                    "todos_database"
                )
                    .addMigrations(MIGRATION_5_6)
                .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // новый device_id при новой БД
                            val id = UUID.randomUUID().toString()
                            db.execSQL(
                                "INSERT OR IGNORE INTO app_meta(key, value) VALUES('device_id', ?)",
                                arrayOf(id)
                            )
                            // дублируем в prefs
                            //DeviceIdProvider.set(id, context)
                        }
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            // deviceId из prefs; если prefs потерялиcь, но БД есть — восстановим
/**                            if (runCatching { DeviceIdProvider.get() }.isFailure) {
                                val cursor = db.query("SELECT value FROM app_meta WHERE key='device_id' LIMIT 1")
                                cursor.use {
                                    if (it.moveToFirst()) {
                                        DeviceIdProvider.set(it.getString(0), context)
                                    } else {
                                        // Странный случай: нет device_id в БД → создадим
                                        Log.wtf("NO DEVICE ID:", "NO DEVICE ID IN PREFS AND DB")
                                        val id = UUID.randomUUID().toString()
                                        db.execSQL(
                                            "INSERT OR IGNORE INTO app_meta(key, value) VALUES('device_id', ?)",
                                            arrayOf(id)
                                        )
                                        DeviceIdProvider.set(id, context)
                                    }
                                }
                            }**/

                            /**
                             * Триггеры для таблиц
                             */
                            //notes
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_notes_ins_outbox
                            AFTER INSERT ON notes BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('notes', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
                            END;
                            """.trimIndent())
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_notes_upd_outbox
                            AFTER UPDATE ON notes BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('notes', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
                            END;
                            """.trimIndent())

                            //note_date_entries
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_note_date_entries_ins_outbox
                            AFTER INSERT ON note_date_entries BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('note_date_entries', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
                            END;
                            """.trimIndent())
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_note_date_entries_upd_outbox
                            AFTER UPDATE ON note_date_entries BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('note_date_entries', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
                            END;
                            """.trimIndent())

                            //note_lists
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_note_lists_ins_outbox
                            AFTER INSERT ON note_lists BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('note_lists', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
                            END;
                            """.trimIndent())
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_note_lists_upd_outbox
                            AFTER UPDATE ON note_lists BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('note_lists', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
                            END;
                            """.trimIndent())

                            //note_tag
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_note_tag_ins_outbox
                            AFTER INSERT ON note_tag BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('note_tag', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
                            END;
                            """.trimIndent())
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_note_tag_upd_outbox
                            AFTER UPDATE ON note_tag BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('note_tag', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
                            END;
                            """.trimIndent())

                            //note_to_tag
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_note_to_tag_ins_outbox
                            AFTER INSERT ON note_to_tag BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('note_to_tag', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
                            END;
                            """.trimIndent())
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_note_to_tag_upd_outbox
                            AFTER UPDATE ON note_to_tag BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('note_to_tag', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
                            END;
                            """.trimIndent())
                        }
                    })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
