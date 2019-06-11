package net.aiscope.gdd_app.ui.newHealthFacility

import android.widget.Toast
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
}