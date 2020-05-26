package net.aiscope.gdd_app.ui.microscope_quality

interface MicroscopeQualityView {

    fun fillForm(model: MicroscopeQualityViewStateModel?)
    fun showRetryBar()
    fun goToCaptureImage(nextImageName: String)
}
