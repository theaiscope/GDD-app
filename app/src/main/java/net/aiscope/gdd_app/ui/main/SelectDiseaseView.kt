package net.aiscope.gdd_app.ui.main

interface SelectDiseaseView {

    fun captureImage(nextImageName: String, nextMaskName: String)
    fun showSuccessToast()
    fun showFailureToast()
}
