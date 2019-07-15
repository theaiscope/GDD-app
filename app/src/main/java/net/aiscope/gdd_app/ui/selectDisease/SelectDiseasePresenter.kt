package net.aiscope.gdd_app.ui.selectDisease

import net.aiscope.gdd_app.model.Disease
import net.aiscope.gdd_app.network.Credentials
import net.aiscope.gdd_app.network.S3Storage
import net.aiscope.gdd_app.network.S3Uploader
import net.aiscope.gdd_app.repository.HospitalRepository
import net.aiscope.gdd_app.repository.SampleRepository
import javax.inject.Inject

class SelectDiseasePresenter @Inject constructor(
    val view: SelectDiseaseView,
    val repository: SampleRepository
) {

    fun saveDisease(input :String) {
        if (!input.isBlank()) {
            val sample = repository.create().copy(disease = input)
            repository.store(sample)

            val samples = repository.all()
            view.startActivity()
            view.showSuccessToast()
        } else {
            view.showFailureToast()
        }
    }

}
