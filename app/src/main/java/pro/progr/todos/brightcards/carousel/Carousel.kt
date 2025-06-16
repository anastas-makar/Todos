package pro.progr.brightcards.carousel

class Carousel<E>(val list: List<E>) {

    private var counter = 0

    fun current(): E {
        return get(counter)
    }

    fun next(): E {
        return get(++counter)
    }

    fun prev(): E {
        return get(--counter)
    }

     fun get(index: Int): E {
        if (list.size == 0) throw RuntimeException("Unable to get element from empty array")

        val remainder = index % list.size

        val swipedIndex = if (remainder >= 0) remainder else list.size + remainder

        return list.get(swipedIndex)
    }

    fun swipeTo(element: E) : Boolean {
        for (i in 0 until list.size) {
            element?.let {
                if(it.equals(get(i))) {
                    counter = i

                    return true
                }
            }
        }

        return false
    }
}