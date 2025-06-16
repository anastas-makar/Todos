package pro.progr.brightcards.colors


class ColorsProvider {
    companion object {
        fun getPalette(paletteType: PaletteType): List<ColorStyle> = when (paletteType) {
//            PaletteType.WATERCOLOR -> TODO()
//            PaletteType.PENCILS -> TODO()
            PaletteType.GOUACHE -> GouachePalette.values().toList()
            PaletteType.PASTEL -> PastelPalette.values().toList()
        }
    }
}