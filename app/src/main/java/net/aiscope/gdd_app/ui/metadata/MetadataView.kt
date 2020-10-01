package net.aiscope.gdd_app.ui.metadata

import net.aiscope.gdd_app.model.CompletedCapture

interface MetadataView {
    fun fillForm(model: ViewStateModel)
    fun captureImage(nextImageName: String)
    fun editCapture(disease: String, capture: CompletedCapture)
    fun finishFlow()
    fun showRetryBar()
}
