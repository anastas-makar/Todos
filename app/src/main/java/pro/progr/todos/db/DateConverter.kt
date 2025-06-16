package pro.progr.todos.db

import androidx.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.getTime()
    }
}