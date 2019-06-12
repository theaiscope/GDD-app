package net.aiscope.gdd_app.ui.newHealthFacility

import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.repository.HospitalRepository
import javax.inject.Inject

class NewHealthFacilityPresenter @Inject constructor(val view: NewHealthFacilityView,
                                                     val repository: HospitalRepository
) {

    fun saveHospital(hospitalName: String) {
        if (hospitalName.isBlank()) {
            repository.store(hospitalName)
            this.showToast(R.string.confirmation_message_health_facility_saved)
        } else {
            this.showToast(R.string.error_message_field_empty)
        }
    }

    fun showToast(messageId: Int) {
        view.showToast(messageId)
    }

    fun destroyActivity() {
        view.destroy()
    }
}