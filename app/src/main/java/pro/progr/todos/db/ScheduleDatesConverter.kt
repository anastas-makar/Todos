package pro.progr.diamondsandberries.db

import androidx.room.TypeConverter
import com.google.gson.Gson

class ScheduleDatesConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromScheduleDates(value: Schedule.Dates?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toScheduleDates(value: String?): Schedule.Dates? {


        return if (value == null) null else gson.fromJson(value, Schedule.Dates::class.java)
    }
 }