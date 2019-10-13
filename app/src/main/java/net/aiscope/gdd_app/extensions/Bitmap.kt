package net.aiscope.gdd_app.extensions

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream

const val MAX_QUALITY = 100

fun Bitmap.writeToFile(file: File) {
    val out = FileOutputStream(file)
    this.compress(Bitmap.CompressFormat.JPEG, MAX_QUALITY, out)
}
