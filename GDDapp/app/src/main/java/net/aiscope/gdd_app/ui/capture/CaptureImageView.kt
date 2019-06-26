package net.aiscope.gdd_app.ui.capture

import android.graphics.Bitmap
import io.fotoapparat.result.BitmapPhoto

interface CaptureImageView {

    fun setPreviewImage(bm: Bitmap?)

    fun takePhoto(cb: (BitmapPhoto?) -> Unit)
}