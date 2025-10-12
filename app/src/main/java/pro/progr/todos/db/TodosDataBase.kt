package pro.progr.todos.db

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import pro.progr.diamondsandberries.db.FilterTypeConverter
import pro.progr.diamondsandberries.db.PatternDatesConverter
import pro.progr.diamondsandberries.db.ScheduleDatesConverter
import pro.progr.todos.util.DeviceIdProvider
import java.util.UUID

@Database(
    entities = [
        Note::class,
        NoteInHistory::class,
        NotesList::class,
        NoteTag::class,
        NoteToTagXRef::class,
        DiamondsCount::class,
        DiamondsLog::class,
        Outbox::class,
        AppMeta::class], version = 4
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

    abstract fun outBoxDao(): OutboxDao

    abstract fun diamondsLogDao(): DiamondsLogDao

    abstract fun appMetDao(): AppMetaDao

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
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // новый device_id при новой БД
                            val id = UUID.randomUUID().toString()
                            db.execSQL(
                                "INSERT OR IGNORE INTO app_meta(key, value) VALUES('device_id', ?)",
                                arrayOf(id)
                            )
                            // дублируем в prefs
                            DeviceIdProvider.set(id, context)
                        }
                        override fun onOpen(db: SupportSQLiteDatabase) {
                            // deviceId из prefs; если prefs потерялиcь, но БД есть — восстановим
                            if (runCatching { DeviceIdProvider.get() }.isFailure) {
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
                            }

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

                            //notes_in_history
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_notes_in_history_ins_outbox
                            AFTER INSERT ON notes_in_history BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('notes_in_history', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
                            END;
                            """.trimIndent())
                            db.execSQL("""
                            CREATE TRIGGER IF NOT EXISTS trg_notes_in_history_upd_outbox
                            AFTER UPDATE ON notes_in_history BEGIN
                              INSERT INTO outbox(table_name,row_id,created_at)
                              VALUES('notes_in_history', NEW.id, CAST(strftime('%s','now') AS INTEGER)*1000);
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