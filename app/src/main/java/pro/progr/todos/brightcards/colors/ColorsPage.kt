package pro.progr.todos.brightcards.colors

import pro.progr.todos.brightcards.carousel.Page

class ColorsPage(val paletteType: PaletteType) : Page {
    override fun getPageName(): String {
        return paletteType.getName()
    }

    override fun equals(other: Any?): Boolean {
        return other is ColorsPage && other.paletteType == paletteType
    }

    override fun hashCode(): Int {
        return paletteType.hashCode()
    }
}