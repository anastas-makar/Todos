package pro.progr.todos.db

import androidx.room.TypeConverter
import com.google.gson.Gson

class LongArrayConverter {

    private val gson = Gson()

    @TypeConverter
    fun fromLongArray(value: ArrayPOJO): String {
        return gson.toJson(value.longArray)
    }

    @TypeConverter
    fun toLongArray(value: String): ArrayPOJO {
        return ArrayPOJO(gson.fromJson(value, Array<Long>::class.java))
    }

}