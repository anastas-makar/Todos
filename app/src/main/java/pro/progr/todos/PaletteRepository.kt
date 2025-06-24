package pro.progr.todos

import android.content.SharedPreferences
import pro.progr.todos.brightcards.colors.ColorsPage
import pro.progr.todos.brightcards.colors.PaletteType
import pro.progr.todos.brightcards.model.PaletteRepository
import javax.inject.Inject

class PaletteRepository @Inject constructor(private val sharedPreferences: SharedPreferences) : PaletteRepository {

    companion object {
        const val SHARED_PREFS_NAME = "palette_prefs"
        private const val DEFAULT_PALETTE_KEY = "default_palette"
    }
    override suspend fun getDefaultPalette(): ColorsPage {
        val paletteName = sharedPreferences.getString(DEFAULT_PALETTE_KEY, PaletteType.PASTEL.paletteName)
        val paletteType = PaletteType.values().find { it.paletteName == paletteName } ?: PaletteType.PASTEL
        return ColorsPage(paletteType)
    }

    override suspend fun updateDefaultPalette(palette: ColorsPage): Boolean {
        val editor = sharedPreferences.edit()
        editor.putString(DEFAULT_PALETTE_KEY, palette.paletteType.paletteName)
        return editor.commit()
    }
}