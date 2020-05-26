package net.aiscope.gdd_app.ui.sample_preparation

interface SamplePreparationView {

    fun fillForm(model: SamplePreparationViewStateModel?)
    fun showRetryBar()
    fun goToMicroscopeQuality()
}
