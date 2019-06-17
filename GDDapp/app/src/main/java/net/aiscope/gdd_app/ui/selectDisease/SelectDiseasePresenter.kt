package net.aiscope.gdd_app.ui.selectDisease

import net.aiscope.gdd_app.model.Disease
import net.aiscope.gdd_app.repository.HospitalRepository
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityView
import javax.inject.Inject


class SelectDiseasePresenter @Inject constructor(
    val view: SelectDiseaseView,
    val repository: HospitalRepository
) {

    fun saveDisease(input :String) {
        if (!input.isBlank()) {
            repository.store(Disease(input))
            view.startActivity()
            view.showSuccessToast()
        } else {
            view.showFailureToast()
        }
    }

}
