package net.aiscope.gdd_app.ui.metadata

import java.io.File

interface MetadataView {
    fun fillForm(model: ViewStateModel)
    fun showInvalidFormError()
    fun captureImage(nextImageName: String)
    fun editImage(disease: String, image: File, mask: File)
    fun finishFlow()
    fun showRetryBar()
}
