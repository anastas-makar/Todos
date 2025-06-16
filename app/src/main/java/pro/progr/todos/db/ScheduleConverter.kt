package pro.progr.diamondsandberries.db

import androidx.room.TypeConverter
import com.google.gson.Gson

class ScheduleConverter {

    @TypeConverter
    fun fromSchedule(schedule: Schedule?) : String {
        val gson = Gson()
        return gson.toJson(schedule)
    }

    @TypeConverter
    fun toSchedule(json: String) : Schedule? {
        val gson = Gson()
        return gson.fromJson(json, Schedule::class.java)
    }
}