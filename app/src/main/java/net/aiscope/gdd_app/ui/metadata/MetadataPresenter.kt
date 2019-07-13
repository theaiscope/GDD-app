package net.aiscope.gdd_app.ui.metadata

import net.aiscope.gdd_app.R
import javax.inject.Inject

data class FieldOption(val id: Long, val title: Int)
data class FieldModel(val title: Int, val options: List<FieldOption>, val required: Boolean = true)

class MetadataPresenter @Inject constructor(
    val view: MetadataView
) {

    fun showScreen() {
        val model = listOf(FieldModel(R.string.metadata_blood_smear_title, listOf(
            FieldOption(1, R.string.metadata_blood_smear_thin),
            FieldOption(2, R.string.metadata_blood_smear_thick)
        )))
        view.fillForm(model)
    }

    fun notValid() {
        view.showInvalidFormError()
    }

    fun save(values: List<Any?>) {
        view.goToHome()
    }
}
