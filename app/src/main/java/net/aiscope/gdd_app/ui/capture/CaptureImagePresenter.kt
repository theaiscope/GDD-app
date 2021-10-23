package net.aiscope.gdd_app.ui.capture

import io.fotoapparat.exception.camera.CameraException
import net.aiscope.gdd_app.repository.SampleRepository
import timber.log.Timber

class CaptureImagePresenter(
    val view: CaptureImageView,
    val repository: SampleRepository
) {
    private var processingImageCapture: Boolean = false

    fun handleCaptureImageButton(imageName: String, viewClassFrom: String) {
        Timber.tag("Taking Photo").d("button pressed")
        if (processingImageCapture){
            Timber.tag("Taking Photo").d("Already processing photo")
            return
        }
        processingImageCapture = true
        view.takePhoto(imageName) { file ->
            try{
                Timber.tag("Taking Photo").d(file?.absolutePath)
                if (file == null) {
                    view.notifyImageCouldNotBeTaken()
                } else {
                    val sample = repository.current().addNewlyCapturedImage(file)
                    repository.store(sample)

                    view.goToMask(sample.disease, file.absolutePath, sample.nextMaskName(), viewClassFrom)
                }
            } finally {
                //Allow new photo to be taken once callback completes
                processingImageCapture = false
            }
        }
    }

    fun onCaptureError(it: CameraException) {
        Timber.tag("Camera Error").e(it, "capture error")
        //Allow new photo to be taken on camera error
        processingImageCapture = false
    }
}
