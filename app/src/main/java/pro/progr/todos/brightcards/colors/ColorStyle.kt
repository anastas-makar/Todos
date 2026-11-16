package pro.progr.todos.brightcards.colors

interface ColorStyle {
    fun backgroundColor() : Long
    fun textColor() : Long
    fun secondaryElementsColor() : Long
    fun iconsColor() : Long
    fun separateIconColor() : Long
    fun separateIconBackgroundColor() : Long
    fun placeHolderColor(): Long
}