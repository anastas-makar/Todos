package pro.progr.todos

import pro.progr.todos.db.SublistChain
import pro.progr.lists.NestedList

class NList(var lname : String = "todo", val sublistChain: SublistChain, override val id : String) : NestedList {
    override fun setName(listName: String) {
        lname = listName
    }

    override fun getName(): String {
        return lname
    }

    override fun getNesting(): Long {
        return sublistChain.size.toLong() - 1
    }
}