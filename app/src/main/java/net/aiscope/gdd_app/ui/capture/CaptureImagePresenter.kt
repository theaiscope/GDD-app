package net.aiscope.gdd_app.ui.capture

import io.fotoapparat.exception.camera.CameraException
import net.aiscope.gdd_app.repository.SampleRepository
import timber.log.Timber

class CaptureImagePresenter(
    val view: CaptureImageView,
    val repository: SampleRepository,
    var processingImageCapture: Boolean = false
) {

    fun handleCaptureImageButton(imageName: String) {
        if (processingImageCapture){
            Timber.tag("Taking Photo").d("Already processing photo")
            return
        }
        processingImageCapture = true
        Timber.tag("Taking Photo").d("button pressed")
        view.takePhoto(imageName) { file ->
            Timber.tag("Taking Photo").d(file?.absolutePath)
            if (file == null) {
                view.notifyImageCouldNotBeTaken()
            } else {
                val sample = repository.current().addNewlyCapturedImage(file)
                repository.store(sample)

                view.goToMask(sample.disease, file.absolutePath, sample.nextMaskName())
            }
            //What if shit falls over tho??
            processingImageCapture = false
        }
    }

    fun onCaptureError(it: CameraException) {
        Timber.tag("Camera Error").e(it, "capture error")
    }
}
