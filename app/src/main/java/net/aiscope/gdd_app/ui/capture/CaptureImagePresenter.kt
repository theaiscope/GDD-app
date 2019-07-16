package net.aiscope.gdd_app.ui.capture

import android.util.Log
import io.fotoapparat.exception.camera.CameraException
import net.aiscope.gdd_app.repository.SampleRepository

class CaptureImagePresenter(val view: CaptureImageView,
                            val repository: SampleRepository) {


    fun handleCaptureImageButton() {
        Log.e("Taking Photo", "button pressed")
        view.takePhoto(repository.current().id) {file ->
            Log.e("Taking Photo", file?.absolutePath)
            if (file == null) {
                view.notifyImageCouldNotBeTaken()
            } else {
                val sample = repository.create().copy(imagePath = file.absolutePath)
                repository.store(sample)

                view.goToMask(sample.imagePath)
            }
        }
    }

    fun onCaptureError(it: CameraException) {
        Log.e("Camera Error", it.message)
    }

}