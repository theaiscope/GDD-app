package net.aiscope.gdd_app.extensions

import android.widget.Spinner

fun Spinner.select(value: String?) {
    (0 until adapter.count)
        .firstOrNull { adapter.getItem(it) == value }
        ?.let { setSelection(it) }
}
