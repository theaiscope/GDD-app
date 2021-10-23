package net.aiscope.gdd_app.ui.capture

import java.io.File

interface CaptureImageView {

    fun takePhoto(imageName: String, onPhotoReceived: suspend (File?) -> Unit)

    fun notifyImageCouldNotBeTaken()

    fun goToMask(diseaseName: String, imagePath: String, nextMaskName: String, viewClassFrom: String)
}
