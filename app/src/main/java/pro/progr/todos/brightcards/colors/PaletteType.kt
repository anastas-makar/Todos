package pro.progr.brightcards.colors

enum class PaletteType(val paletteName: String) {
    /*WATERCOLOR("Watercolor"), PENCILS("Pencils"),*/ GOUACHE("Gouache"), PASTEL("Pastel");

    fun getName(): String {
        return paletteName
    }
}