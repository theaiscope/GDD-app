package net.aiscope.gdd_app.ui.capture

import android.util.Log
import io.fotoapparat.exception.camera.CameraException
import io.fotoapparat.result.BitmapPhoto

class CaptureImagePresenter(val view: CaptureImageView) {

    lateinit var imageBitmap: BitmapPhoto

    fun handleCaptureImageButton() {
        Log.e("Taking Photo", "button pressed")
        view.takePhoto {
            Log.e("Taking Photo", it.toString())
            storeImageData(it)
            view.setPreviewImage(imageBitmap)
        }
    }

    private fun storeImageData(photo: BitmapPhoto?) {
        if (photo != null) {
            imageBitmap = photo
        }
    }

    fun onCaptureError(it: CameraException) {
        Log.e("Camera Error", it.message)
    }

}