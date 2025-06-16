package pro.progr.diamondsandberries.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class PatternDatesConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromPatternDates(value: LinkedHashMap<Long, Schedule.Day>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toPatternDates(value: String?): LinkedHashMap<Long, Schedule.Day>? {
        val patternDaysType: Type = object : TypeToken<LinkedHashMap<Long, Schedule.Day>>() {}.type
        return if (value == null) null else gson.fromJson(value, patternDaysType)
    }

 }