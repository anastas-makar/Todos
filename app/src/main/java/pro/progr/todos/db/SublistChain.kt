package pro.progr.doflow.db

const val FIRST_LEVEL_CHAIN = "1"
const val DELIMETER = ":"

class SublistChain(
    val sublistsString : String = FIRST_LEVEL_CHAIN
) {
    val sublistsList get() = sublistsString.split(DELIMETER)

    override fun toString(): String {
        return sublistsString
    }

    val size get() = sublistsList.size

    override fun equals(other: Any?): Boolean {
        return sublistsString.equals((other as SublistChain).sublistsString)
    }

    fun containsAll() : Boolean {
        return sublistsString.equals(FIRST_LEVEL_CHAIN)
    }

    fun isFirstLevel() : Boolean {
        return sublistsString.equals(FIRST_LEVEL_CHAIN)
    }

    fun getParent() : SublistChain? {
        if (isFirstLevel()) {
            return null
        }

        return SublistChain(
            sublistsList.dropLast(1).joinToString(separator = DELIMETER)
        )
    }

    companion object {
        val sublistChainComparator = Comparator<SublistChain> { a, b ->

            val aNums = a.sublistsList
            val bNums = b.sublistsList

            when {
                (aNums.size == 0 && bNums.size == 0) -> 0
                (aNums.size == 0) -> -1
                (bNums.size == 0) -> 1
                (aNums.get(0) > bNums.get(0)) -> 1
                (aNums.get(0) < bNums.get(0)) -> -1
                else -> compareSortNumArrays(aNums, bNums)
            }
        }

        private fun compareSortNumArrays(aNums : List<String>, bNums : List<String>) : Int {
            for (i in 1 .. (aNums.size - 1)) {
                if (i >= bNums.size) {
                    return 1
                }

                if (aNums.get(i) < bNums.get(i)) {
                    return -1
                }

                if (aNums.get(i) > bNums.get(i)) {
                    return 1
                }
            }

            return -1
        }
    }

}