package net.aiscope.gdd_app.ui.mask

import net.aiscope.gdd_app.repository.SampleRepository

class MaskPresenter(
    val view: MaskView,
    val repository: SampleRepository
) {

    fun handleCaptureBitmap(maskName: String) {
        view.takeMask(maskName) { file ->
            if (file == null) {
                view.notifyImageCouldNotBeTaken()
            } else {
                val sample = repository.current().addMask(file)
                repository.store(sample)

                view.goToMetadata()
            }
        }
    }

    fun start(imagePath: String?) {
        if (imagePath == null) {
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

    fun moveMode() {
        view.moveMode()
    }
}
