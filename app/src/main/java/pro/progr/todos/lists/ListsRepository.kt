package pro.progr.lists

import kotlinx.coroutines.flow.Flow

interface ListsRepository {
    fun getListOfLists() : Flow<List<NestedList>>

    fun selectList(list: NestedList)

    fun addSublistToList(list: NestedList) : String

    fun deleteList(list: NestedList)

    fun updateList(list: NestedList)

    /**
     * TODO: когда SublistChain будет перенесён в библиотеку lists,
     * нужно избавиться от путаницы и передавать SublistChain вместо listId : String
     */
    fun bindListToEntity(listId : String, entityId : String)

    fun findParent(list: NestedList, value: List<NestedList>): NestedList?
}