package net.aiscope.gdd_app.ui.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.aiscope.gdd_app.extensions.writeToFile
import java.io.File

object BitmapReader {
    suspend fun decodeSampledBitmapFromResource(
        image: File,
        mutable: Boolean,
        request: DownSamplingRequest
    ): Bitmap = BitmapFactory.Options().run {
        // First decode with inJustDecodeBounds=true to check dimensions
        inJustDecodeBounds = true
        BitmapFactory.decodeFile(image.absolutePath, this)

        inMutable = mutable
        inPreferredConfig = Bitmap.Config.ARGB_8888

        // Calculate inSampleSize
        inSampleSize = calculateInSampleSize(this, request)

        // Decode bitmap with inSampleSize set
        inJustDecodeBounds = false

        val bitmap = BitmapFactory.decodeFile(image.absolutePath, this)

        bitmap
    }

    suspend fun decodeSampledBitmapAndCache(
        image: File,
        reqWidth: Int,
        reqHeight: Int,
        cacheDir: File
    ): Bitmap = withContext(Dispatchers.IO) {
        val cachedImage = File(
            cacheDir,
            "${image.nameWithoutExtension}_${reqWidth}x${reqHeight}.${image.extension}"
        )
        if (cachedImage.exists()) {
            return@withContext BitmapFactory.decodeFile(cachedImage.absolutePath)
        }
        val bitmap = BitmapReader.decodeSampledBitmapFromResource(
            image,
            false,
            MinimumSizeDownSampling(reqWidth, reqHeight)
        )

        //Write to cache for future access
        bitmap.writeToFile(cachedImage, Bitmap.CompressFormat.JPEG)

        bitmap
    }
}

suspend fun calculateInSampleSize(
    options: BitmapFactory.Options,
    downSamplingRequest: DownSamplingRequest
): Int = withContext(Dispatchers.Default) {
    // Raw height and width of image
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > downSamplingRequest.height || width > downSamplingRequest.width) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        when (downSamplingRequest) {
            is MinimumSizeDownSampling -> {
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width LARGER OR EQUAL than the requested height and width.
                while (halfHeight / inSampleSize >= downSamplingRequest.height
                    && halfWidth / inSampleSize >= downSamplingRequest.width) {
                    inSampleSize *= 2
                }
            }
            is MaximumSizeDownSampling -> {
                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width SMALLER OR EQUAL than the maximum height and width.
                while (halfHeight / inSampleSize > downSamplingRequest.height
                    || halfWidth / inSampleSize > downSamplingRequest.width) {
                    inSampleSize *= 2
                }
                //And once more so we end up beneath the maximum
                inSampleSize *= 2
            }
        }
    }
    return@withContext inSampleSize
}
