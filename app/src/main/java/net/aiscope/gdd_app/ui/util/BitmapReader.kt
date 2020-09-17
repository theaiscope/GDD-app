package net.aiscope.gdd_app.ui.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.aiscope.gdd_app.extensions.writeToFile
import timber.log.Timber
import java.io.File
import javax.microedition.khronos.egl.EGL10
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.egl.EGLContext
import javax.microedition.khronos.egl.EGLDisplay
import kotlin.math.max

object BitmapReader {
    // Texture size should never be smaller than this
    private const val DEFAULT_MIN_TEXTURE_SIZE = 2048

    val MAX_TEXTURE_SIZE by lazy { getMaxTextureSize() }

    suspend fun decodeSampledBitmapFromResource(
        image: File,
        request: DownSamplingRequest,
        mutable: Boolean
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
        val bitmap = decodeSampledBitmapFromResource(
            image,
            MinimumSizeDownSampling(reqWidth, reqHeight),
            mutable = false
        )

        //Write to cache for future access
        bitmap.writeToFile(cachedImage, Bitmap.CompressFormat.JPEG)

        bitmap
    }

    private fun getMaxTextureSize(): Int {
        // Get EGL Display
        val egl = EGLContext.getEGL() as EGL10
        val display: EGLDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY)

        // Initialise
        val version = IntArray(2)
        egl.eglInitialize(display, version)

        // Query total number of configurations
        val totalConfigurations = IntArray(1)
        egl.eglGetConfigs(display, null, 0, totalConfigurations)

        // Query actual list configurations
        val configurationsList: Array<EGLConfig?> = arrayOfNulls(totalConfigurations[0])
        egl.eglGetConfigs(display, configurationsList, totalConfigurations[0], totalConfigurations)
        val textureSize = IntArray(1)
        var maximumTextureSize = 0

        // Iterate through all the configurations to located the maximum texture size
        for (i in 0 until totalConfigurations[0]) {
            // Only need to check for width since opengl textures are always squared
            egl.eglGetConfigAttrib(
                display,
                configurationsList[i],
                EGL10.EGL_MAX_PBUFFER_WIDTH,
                textureSize
            )

            // Keep track of the maximum texture size
            if (maximumTextureSize < textureSize[0]) maximumTextureSize = textureSize[0]
        }

        // Release
        egl.eglTerminate(display)

        Timber.tag("Downsampling").d("Setting maximum texture size to " +
                "${max(maximumTextureSize, DEFAULT_MIN_TEXTURE_SIZE)}")

        // Return largest texture size found, or default
        return max(maximumTextureSize, DEFAULT_MIN_TEXTURE_SIZE)
    }
}

suspend fun calculateInSampleSize(
    options: BitmapFactory.Options,
    downSamplingRequest: DownSamplingRequest
): Int = withContext(Dispatchers.Default) {
    // Raw height and width of image
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    Timber.tag("Downsampling").d("Original dimensions $height x $width")
    Timber.tag("Downsampling").d("Requested dimensions ${downSamplingRequest.height} x ${downSamplingRequest.width}")
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
    Timber.tag("Downsampling").d("Resulting sample size $inSampleSize")
    return@withContext inSampleSize
}
