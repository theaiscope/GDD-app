package net.aiscope.gdd_app.ui.newHealthFacility

import android.widget.EditText

interface NewHealthFacilityPresenter {
    fun setView(newHealthFacilityActivity: NewHealthFacilityActivity)
    fun destroyActivity()
    fun handleSaveButtonClick(healthFacilityText: EditText): Unit
    fun saveHospital(hospitalName: String)
    fun showToast(messageId: Int)
}