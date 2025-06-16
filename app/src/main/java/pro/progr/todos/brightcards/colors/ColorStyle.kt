package pro.progr.brightcards.colors

interface ColorStyle {
    fun backgroundColor() : Long
    fun textColor() : Long
    fun secondaryElementsColor() : Long
    fun iconsColor() : Long
    fun placeHolderColor(): Long
}