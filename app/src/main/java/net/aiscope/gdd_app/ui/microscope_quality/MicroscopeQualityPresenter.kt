package net.aiscope.gdd_app.ui.microscope_quality

import android.content.Context
import net.aiscope.gdd_app.repository.SampleRepository
import timber.log.Timber
import javax.inject.Inject

class MicroscopeQualityPresenter @Inject constructor(
    val view: MicroscopeQualityView,
    private val mapper: MicroscopeQualityMapper,
    val repository: SampleRepository,
    private val context: Context
) {

    //TODO: so this needs to be done on the fragment too
    //(Or rather on the new activity??)
    suspend fun showScreen() {
        val lastMicroscopeQuality = repository.lastSaved()?.microscopeQuality
        view.fillForm(mapper.convert(lastMicroscopeQuality))
    }

    suspend fun save(viewModel: MicroscopeQualityViewStateModel) {
        val model = mapper.convert(viewModel)
        try {
            val sample = repository.current()
                .copy(microscopeQuality = model)
            repository.store(sample)
            view.goToCaptureImage(sample.nextImageName())
        } catch(@Suppress("TooGenericExceptionCaught") error: Throwable) {
            Timber.e(error, "An error occurred when saving sample preparation")
            view.showRetryBar()
        }
    }
}

data class MicroscopeQualityViewStateModel(
    val isDamaged: Boolean,
    val magnification: Int
)
