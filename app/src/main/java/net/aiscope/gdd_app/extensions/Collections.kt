package net.aiscope.gdd_app.extensions

operator fun <T> LinkedHashSet<T>.plus(element: T): LinkedHashSet<T> {
    val result = LinkedHashSet<T>()
    result.addAll(this)
    result.add(element)
    return result
}

fun <T> Iterable<T>.toLinkedHashSet(): LinkedHashSet<T> {
    return toCollection(LinkedHashSet())
}
