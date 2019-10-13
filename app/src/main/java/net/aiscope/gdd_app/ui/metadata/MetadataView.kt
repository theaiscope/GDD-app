package net.aiscope.gdd_app.ui.metadata

interface MetadataView {
    fun fillForm(model: ViewStateModel)
    fun showInvalidFormError()
    fun finishFlow()
    fun captureImage(nextImageName: String, nextMaskName: String)
}
