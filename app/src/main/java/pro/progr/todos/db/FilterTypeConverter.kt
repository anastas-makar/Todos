package pro.progr.diamondsandberries.db

import androidx.room.TypeConverter
import pro.progr.todos.datefilters.FilterType

class FilterTypeConverter {

    @TypeConverter
    fun fromFilterType(value: FilterType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toFilterType(value: String?): FilterType? {
        return if (value == null) null else FilterType.valueOf(value)
    }

 }