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

//        val imagePath = "/data/user/0/net.aiscope.gdd_app/files/c6c725a5-6eab-4800-823c-39c582b5ac70.jpg"
        if ( imagePath == null ) {
            view.notifyImageCouldNotBeTaken()
        } else {
            view.loadBitmap(imagePath)
        }

    }


}