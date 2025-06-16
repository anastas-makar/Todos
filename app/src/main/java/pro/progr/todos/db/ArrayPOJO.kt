package pro.progr.todos.db

data class ArrayPOJO(
    val longArray : Array<Long>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ArrayPOJO

        if (!longArray.contentEquals(other.longArray)) return false

        return true
    }

    override fun hashCode(): Int {
        return longArray.contentHashCode()
    }
}