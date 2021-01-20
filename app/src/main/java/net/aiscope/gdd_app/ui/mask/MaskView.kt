package net.aiscope.gdd_app.ui.mask

import java.io.File

interface MaskView {

    fun takeMask(maskName: String, onPhotoReceived: suspend (File?) -> Unit)
    fun goToMetadata()
    fun goToSampleCompletion()
    fun notifyImageCouldNotBeTaken()
    fun initPhotoMaskView(imagePath: String, maskPath: String?)
}
