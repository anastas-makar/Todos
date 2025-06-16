package pro.progr.brightcards.model

import pro.progr.brightcards.colors.ColorsPage

interface PaletteRepository {

    suspend fun getDefaultPalette(): ColorsPage

    suspend fun updateDefaultPalette(palette: ColorsPage): Boolean
}