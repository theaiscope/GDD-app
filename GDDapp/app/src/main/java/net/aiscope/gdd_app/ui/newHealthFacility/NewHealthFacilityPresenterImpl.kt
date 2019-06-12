package net.aiscope.gdd_app.ui.newHealthFacility

import android.widget.EditText
import android.widget.Toast
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.repository.HospitalRepository
import net.aiscope.gdd_app.repository.SharedPreferencesRepository

class NewHealthFacilityPresenterImpl : NewHealthFacilityPresenter {

    private lateinit var newHealthFacilityActivity: NewHealthFacilityActivity
    private lateinit var healthFacilityRepository: HospitalRepository

    override fun setView(newHealthFacilityActivity: NewHealthFacilityActivity) {
        this.newHealthFacilityActivity = newHealthFacilityActivity
        this.healthFacilityRepository = SharedPreferencesRepository(newHealthFacilityActivity)
    }

    override fun saveHospital(hospitalName: String) {
        healthFacilityRepository.store(hospitalName)
    }

    override fun showToast(messageId: Int) {
        val toast = Toast.makeText(newHealthFacilityActivity, messageId, Toast.LENGTH_SHORT)
        toast.show()
    }

    override fun handleSaveButtonClick(healthFacilityText: EditText) {
        if (healthFacilityText.text.isNotEmpty()) {
            this.saveHospital(healthFacilityText.text.toString())
            this.showToast(R.string.confirmation_message_health_facility_saved)
        } else {
            this.showToast(R.string.error_message_field_empty)
        }
    }

    override fun destroyActivity() {
        this.newHealthFacilityActivity.finish()
    }
}