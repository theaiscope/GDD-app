package net.aiscope.gdd_app.ui.selectDisease

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

            view.captureImage(sample.nextImageName(), sample.nextMaskName())
            view.showSuccessToast()
        } else {
            view.showFailureToast()
        }
    }
}
