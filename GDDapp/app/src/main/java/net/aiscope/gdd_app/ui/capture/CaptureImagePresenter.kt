package net.aiscope.gdd_app.ui.capture

import android.graphics.Bitmap
import android.util.Log
import io.fotoapparat.result.BitmapPhoto

class CaptureImagePresenter(val view: CaptureImageView) {

    lateinit var imageBitmap: Bitmap

    fun handleCaptureImageButton() {
        Log.e("Taking Photop", "button pressed")
        view.takePhoto {
            storeImageData(it)
            view.setPreviewImage(imageBitmap)
        }
    }

    fun storeImageData(photo: BitmapPhoto?) {
        Log.e("Taking Photop", photo.toString())
        if (photo != null) {
            imageBitmap = photo.bitmap
        }
    }

}