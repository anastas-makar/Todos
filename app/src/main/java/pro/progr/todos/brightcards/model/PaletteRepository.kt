package pro.progr.todos.brightcards.model

import pro.progr.todos.brightcards.colors.ColorsPage

interface PaletteRepository {

    suspend fun getDefaultPalette(): ColorsPage

    suspend fun updateDefaultPalette(palette: ColorsPage): Boolean
}