package pro.progr.doflow.db

import androidx.room.TypeConverter
import pro.progr.brightcards.colors.ColorStyle
import pro.progr.brightcards.colors.GouachePalette
import pro.progr.brightcards.colors.PastelPalette

class ColorStyleConverter {

    @TypeConverter
    fun fromColorStyle(style: ColorStyle): String {
        return when (style) {
            is GouachePalette -> "GouachePalette_${style.name}"
            is PastelPalette -> "PastelPalette_${style.name}"
            // другие типы ColorStyle можно добавить здесь
            else -> throw IllegalArgumentException("Could not recognize style")
        }
    }

    @TypeConverter
    fun toColorStyle(style: String): ColorStyle {
        return when {
            style.startsWith("GouachePalette_") -> {
                GouachePalette.valueOf(style.removePrefix("GouachePalette_"))
            }
            style.startsWith("PastelPalette_") -> {
                PastelPalette.valueOf(style.removePrefix("PastelPalette_"))
            }
            // другие типы ColorStyle можно добавить здесь
            else -> throw IllegalArgumentException("Could not recognize style")
        }
    }
}
