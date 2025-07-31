package pro.progr.lists

interface NestedList {
    val id: String
    fun setName(listName : String)
    fun getName() : String
    fun getNesting() : Long
}