package net.aiscope.gdd_app.ui.metadata

interface MetadataView {
    fun fillForm(model: ViewStateModel)
    fun showInvalidFormError()
    fun captureImage(nextImageName: String)
    fun finishFlow()
    fun showRetryBar()
}
