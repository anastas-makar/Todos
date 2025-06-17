package pro.progr.todos.brightcards.colors

enum class PastelPalette(val rgb: Long) : ColorStyle {
    PINK(0xffFFCDD2),
    PINKY(0xffF8BBD0),
    PINK_VIOL(0xffE1BEE7),
    VIOL_PINK(0xffD1C4E9),
    VIOL_BLUE(0xffC5CAE9),
    BLUE_VIOL(0xffBBDEFB),
    BLUE(0xffB3E5FC),
    LIGHT_BLUE(0xffB2EBF2),
    BLUE_GREEN(0xffB2DFDB),
    GREEN(0xffC8E6C9),
    LIGHT_GREEN(0xffDCEDC8),
    YELLOW_GREEN(0xffF0F4C3),
    YELLOW(0xffFFF9C4),
    YELLOW_ORANGE(0xffFFECB3),
    ORANGE(0xffFFE0B2),
    PINK_ORANGE(0xffFFCCBC);

    override fun backgroundColor() : Long {
        return rgb
    }

    override fun textColor() : Long {
        return 0xff000000
    }

    override fun secondaryElementsColor(): Long {
        return 0x55000000
    }

    override fun iconsColor(): Long {
        return 0x99000000
    }

    override fun placeHolderColor(): Long {
        return 0x99000000
    }
}