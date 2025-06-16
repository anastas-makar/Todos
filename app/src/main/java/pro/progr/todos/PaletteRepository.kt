package pro.progr.doflow

import android.content.SharedPreferences
import pro.progr.brightcards.colors.ColorsPage
import pro.progr.brightcards.colors.PaletteType
import pro.progr.brightcards.model.PaletteRepository

class PaletteRepository(private val sharedPreferences: SharedPreferences) : PaletteRepository {

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