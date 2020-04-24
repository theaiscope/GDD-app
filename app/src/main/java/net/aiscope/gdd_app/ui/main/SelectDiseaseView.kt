package net.aiscope.gdd_app.ui.main

interface SelectDiseaseView {

    fun captureImage(nextImageName: String)
    fun showSuccessToast()
    fun showFailureToast()
    fun logout(success: Boolean)
}
