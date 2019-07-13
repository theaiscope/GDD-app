package net.aiscope.gdd_app.ui.capture

import io.fotoapparat.result.BitmapPhoto

interface CaptureImageView {

    fun setPreviewImage(photo: BitmapPhoto)

    fun takePhoto(onPhotoReceived: (BitmapPhoto?) -> Unit)

    fun notifyImageCouldNotBeTaken()

    fun goToMetadata()
}