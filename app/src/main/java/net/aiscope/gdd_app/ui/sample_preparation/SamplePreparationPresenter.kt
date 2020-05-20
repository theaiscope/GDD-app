package net.aiscope.gdd_app.ui.sample_preparation

import android.content.Context
import net.aiscope.gdd_app.repository.SampleRepository
import timber.log.Timber
import javax.inject.Inject

class SamplePreparationPresenter @Inject constructor(
    val view: SamplePreparationView,
    private val mapper: SamplePreparationMapper,
    val repository: SampleRepository,
    private val context: Context
) {

    suspend fun showScreen() {
        val lastPreparation = repository.last()?.preparation
        view.fillForm(mapper.convert(lastPreparation, context))
    }

    suspend fun save(viewModel: SamplePreparationViewStateModel) {
        val model = mapper.convert(viewModel, context)
        try {
            val sample = repository.current()
                .copy(preparation = model)
            repository.store(sample)
            view.goToMicroscopeQuality()
        } catch(@Suppress("TooGenericExceptionCaught") error: Throwable) {
            Timber.e(error, "An error occurred when saving sample preparation")
            view.showRetryBar()
        }
    }
}

data class SamplePreparationViewStateModel(
    val waterType: String,
    val usesGiemsa: Boolean,
    val giemsaFP: Boolean,
    val usesPbs: Boolean,
    val usesAlcohol: Boolean,
    val reusesSlides: Boolean
)
