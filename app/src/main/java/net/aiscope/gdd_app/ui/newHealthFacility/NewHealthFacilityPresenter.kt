package net.aiscope.gdd_app.ui.newHealthFacility

import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.HealthFacility
import net.aiscope.gdd_app.repository.HospitalRepository
import java.util.*
import javax.inject.Inject

class NewHealthFacilityPresenter @Inject constructor(val view: NewHealthFacilityView,
                                                     val repository: HospitalRepository
) {

    fun saveHospital(hospitalName: String) {
        if (!hospitalName.isBlank()) {
            val facility = HealthFacility(hospitalName, UUID.randomUUID().toString())
            repository.store(facility)
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