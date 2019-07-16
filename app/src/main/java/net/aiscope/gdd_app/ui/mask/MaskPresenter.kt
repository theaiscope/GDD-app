package net.aiscope.gdd_app.ui.mask

import android.util.Log
import io.fotoapparat.exception.camera.CameraException
import net.aiscope.gdd_app.repository.SampleRepository

class MaskPresenter(val view: MaskView,
                    val repository: SampleRepository) {

    val id = repository.current().id

    fun handleCaptureBitmap() {
        view.takeMask(id) { file ->
            if (file == null) {
                view.notifyImageCouldNotBeTaken()
            } else {
                val sample = repository.current().copy(maskPath = file.absolutePath)
                repository.store(sample)

                view.goToMetadata()
            }
        }
    }

    fun start() {
        val imagePath = repository.current().imagePath

        if ( imagePath == null ) {
            view.notifyImageCouldNotBeTaken()
        } else {
            view.loadBitmap(imagePath)
        }

    }

    fun eraseMode() {
        view.eraseMode()
    }

    fun brushMode() {
        view.brushMode()
    }
}