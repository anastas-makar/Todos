package pro.progr.brightcards.colors

enum class GouachePalette(val rgb: Long) : ColorStyle {
    LIME_PIE(0xff2f4f2f),
    ORANGE_BROWN(0xffc5491d),
    DARK_GREY(0xff2b2836),
    CARMIN(0xff52101e),
    RED_PURPLE(0xff4d143c),
    RED(0xffb71c1c),
    PURPLE(0xff880e4f),
    VIOLET(0xff4a148c),
    DEEP_BLUE(0xff311b92),
    DARK_BLUE(0xff1A237E),
    BLUE(0xff0D47A1),
    BLUE_L(0xff01579B),
    BLUE_GREEN(0xff006064),
    GREEN(0xff004D40),
    DEEP_GREEN(0xff1B5E20),
    ORANGE(0xffBF360C);

    override fun backgroundColor() : Long {
        return rgb
    }

    override fun textColor() : Long {
        return 0xffffffff
    }

    override fun secondaryElementsColor(): Long {
        return 0xffffffff
    }

    override fun iconsColor(): Long {
        return 0xffffffff
    }

    override fun placeHolderColor(): Long {
        return 0x99ffffff
    }
}