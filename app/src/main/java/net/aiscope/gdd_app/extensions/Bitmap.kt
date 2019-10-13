package net.aiscope.gdd_app.extensions

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

fun Bitmap.writeToFile(file: File) {
    val out = FileOutputStream(file)
    @Suppress("MagicNumber")
    this.compress(Bitmap.CompressFormat.JPEG, 100, out)
}
