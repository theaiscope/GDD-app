package net.aiscope.gdd_app.ui.capture

import android.util.Log
import io.fotoapparat.exception.camera.CameraException
import io.fotoapparat.result.BitmapPhoto

class CaptureImagePresenter(val view: CaptureImageView) {

    lateinit var imageBitmap: BitmapPhoto

    fun handleCaptureImageButton() {
        Log.e("Taking Photo", "button pressed")
        view.takePhoto {photo ->
            Log.e("Taking Photo", photo.toString())
            if (photo == null) {
                view.notifyImageCouldNotBeTaken()
            } else {
                imageBitmap = photo
                view.setPreviewImage(imageBitmap)

                view.goToMetadata()
            }
        }
    }

    fun onCaptureError(it: CameraException) {
        Log.e("Camera Error", it.message)
    }

}