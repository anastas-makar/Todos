package pro.progr.doflow.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import pro.progr.diamondsandberries.db.Schedule

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Изменения в схеме базы данных
        db.execSQL("ALTER TABLE notes ADD COLUMN added_dates TEXT")
        db.execSQL("ALTER TABLE notes ADD COLUMN cancelled_dates TEXT")
        db.execSQL("ALTER TABLE notes ADD COLUMN pattern_days TEXT")
        db.execSQL("ALTER TABLE notes ADD COLUMN pattern_type TEXT")
        db.execSQL("ALTER TABLE notes ADD COLUMN date_since INTEGER")
        db.execSQL("ALTER TABLE notes ADD COLUMN date_till INTEGER")


        // Перенос данных из старой схемы в новую
        val cursor = db.query("SELECT * FROM notes")
        val scheduleIndex = cursor.getColumnIndex("schedule")
        val idIndex = cursor.getColumnIndex("id")

        if (scheduleIndex > -1 && idIndex > -1) {
            while (cursor.moveToNext()) {
                // Извлеките данные schedule как строку JSON
                val scheduleJson = cursor.getString(scheduleIndex)
                val noteId = cursor.getLong(idIndex)

                // Конвертируйте JSON в объект Schedule
                val schedule = Gson().fromJson(scheduleJson, Schedule::class.java)

                // Извлеките данные из объекта Schedule
                val addedDatesJson = Gson().toJson(schedule?.dates?.addedDates)
                val cancelledDatesJson = Gson().toJson(schedule?.dates?.cancelledDates)
                val patternDaysJson = Gson().toJson(schedule?.pattern?.days)
                val patternType = schedule?.pattern?.type.toString()

                // Обновите заметку новыми данными
                db.execSQL("UPDATE notes SET added_dates = ?, " +
                        "cancelled_dates = ?, " +
                        "pattern_days = ?, " +
                        "pattern_type = ?, " +
                        "date_since = ?, " +
                        "date_till = ? " +
                        "WHERE id = ?",
                    arrayOf(addedDatesJson, cancelledDatesJson, patternDaysJson, patternType, noteId))
            }

        }
        cursor.close()
    }
}

val MIGRATION_2_3: Migration = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE notes_in_history ADD COLUMN edited INTEGER NOT NULL DEFAULT 0")
    }

}

val MIGRATION_3_4: Migration = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_notes_in_history_date_noteId ON notes_in_history (date, noteId)")
    }

}

val MIGRATION_1_3 = object : Migration(1, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        MIGRATION_1_2.migrate(db)
        MIGRATION_2_3.migrate(db)
    }

}

val MIGRATION_1_4 = object : Migration(1, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        MIGRATION_1_2.migrate(db)
        MIGRATION_2_3.migrate(db)
        MIGRATION_3_4.migrate(db)
    }

}

val MIGRATION_2_4 = object : Migration(2, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        MIGRATION_2_3.migrate(db)
        MIGRATION_3_4.migrate(db)
    }

}