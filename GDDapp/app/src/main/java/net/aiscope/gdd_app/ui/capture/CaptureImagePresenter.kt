package net.aiscope.gdd_app.ui.capture

import android.graphics.BitmapFactory
import android.util.Log

class CaptureImagePresenter(val view: CaptureImageView) {

    var byteImageArray: ByteArray = byteArrayOf()

    init {
        view.onCameraError = { e: String? ->
            Log.e("Camera Error", e.orEmpty())
        }
    }

    fun handleCaptureImageButton() {
        view.takePhoto()
    }

    fun storeImageData(bytes: ByteArray) {
        byteImageArray = bytes
    }

    fun setPreviewImage(bytes: ByteArray) {
        view.setPreviewImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.size))
    }
}