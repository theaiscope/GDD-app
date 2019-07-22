package net.aiscope.gdd_app.ui.mask

import java.io.File

interface MaskView {

    fun takeMask(id: String, onPhotoReceived: (File?) -> Unit)

    fun goToMetadata()
    fun notifyImageCouldNotBeTaken()
    fun loadBitmap(imagePath: String)
    fun eraseMode()
    fun brushMode()
    fun moveMode()
}