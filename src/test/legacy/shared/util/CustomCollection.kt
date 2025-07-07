package kr.co.marketbill.marketbillcoreserver.legacy.shared.util

inline fun <T, K> Iterable<T>.groupFillBy(keys : Iterable<K>, keySelector: (T) -> K): Map<K, List<T>> {
    val groups = LinkedHashMap<K, MutableList<T>>()
    for (key in keys){
        groups[key] = mutableListOf()
    }
    for (element in this) {
        val key : K = keySelector(element)
        val list = groups.getOrPut(key) { ArrayList<T>() }
        list.add(element)
    }
    return groups
}