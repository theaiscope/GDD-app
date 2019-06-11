package net.aiscope.gdd_app.ui.newHealthFacility

interface NewHealthFacilityPresenter {
    fun setView(newHealthFacilityActivity: NewHealthFacilityActivity)
    fun saveHospital(hospitalName: String)
    fun showToast(messageId: Int)
}