package net.aiscope.gdd_app.ui.capture

import io.fotoapparat.exception.camera.CameraException
import net.aiscope.gdd_app.repository.SampleRepository
import timber.log.Timber

class CaptureImagePresenter(
    val view: CaptureImageView,
    val repository: SampleRepository
) {

    fun handleCaptureImageButton() {
        Timber.tag("Taking Photo").d("button pressed")
        view.takePhoto(repository.current().id) { file ->
            Timber.tag("Taking Photo").d(file?.absolutePath)
            if (file == null) {
                view.notifyImageCouldNotBeTaken()
            } else {
                val sample = repository.current().copy(imagePath = file.absolutePath)
                repository.store(sample)

                view.goToMask(sample.imagePath)
            }
        }
    }

    fun onCaptureError(it: CameraException) {
        Timber.tag("Camera Error").e(it, "capture error")
    }

}
