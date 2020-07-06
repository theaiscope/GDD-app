package net.aiscope.gdd_app.extensions

import android.graphics.Bitmap
import android.graphics.Matrix
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

const val MAX_QUALITY = 100

fun Bitmap.writeToFile(file: File) {
    val out = FileOutputStream(file)
    this.compress(Bitmap.CompressFormat.PNG, MAX_QUALITY, out)
}

suspend fun Bitmap.writeToFileAsync(file: File) = withContext(Dispatchers.IO) {
    writeToFile(file)
}

suspend fun Bitmap.rotate(degrees: Float): Bitmap = withContext(Dispatchers.Default) {
    val matrix = Matrix().apply { postRotate(degrees) }
    Bitmap.createBitmap(this@rotate, 0, 0, width, height, matrix, true)
}
