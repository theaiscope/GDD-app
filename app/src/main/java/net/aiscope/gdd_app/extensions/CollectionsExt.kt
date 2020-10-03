package net.aiscope.gdd_app.extensions


fun <T> List<T>.replaceElementAt(index: Int, value: T) =
    this.slice(0 until index) + value + this.slice(index + 1 until this.size)
