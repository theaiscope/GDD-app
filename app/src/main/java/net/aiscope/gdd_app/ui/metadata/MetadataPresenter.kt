package net.aiscope.gdd_app.ui.metadata

import android.content.Context
import net.aiscope.gdd_app.model.CompletedCapture
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SampleStatus
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.sample_completion.MetadataMapper
import timber.log.Timber
import javax.inject.Inject

data class FieldOption(val id: Long, val title: Int)
data class ViewStateModel(
    val disease: String,
    val captures: List<CompletedCapture>,
    val options: List<FieldOption>,
    val required: Boolean = true,
    val smearTypeId: Int? = null,
    val speciesValue: String? = null,
    val comments: String? = null
)

class MetadataPresenter @Inject constructor(
    private val view: MetadataView,
    private val repository: SampleRepository,
    private val metadataMapper: MetadataMapper,
    private val remoteStorage: RemoteStorage,
    private val context: Context
) {

    suspend fun showScreen() {
        val sample = repository.current()

        val lastMetadata = repository.lastSaved()?.metadata
        view.fillForm(
            ViewStateModel(
                sample.disease,
                sample.captures.completedCaptures,
                emptyList(),
                smearTypeId = lastMetadata?.let { metadataMapper.getSmearTypeId(it.smearType) },
                speciesValue = lastMetadata?.let { metadataMapper.getSpeciesValue(context, it.species) },
                comments = lastMetadata?.comments
            )
        )
    }

    suspend fun save(smearTypeId: Int, speciesValue: String, comments: String) {
        try {
            val sample = repository.current()
                .copy(metadata = SampleMetadata(
                    metadataMapper.getSmearType(smearTypeId),
                    metadataMapper.getSpecies(context, speciesValue),
                    comments
                ), status = SampleStatus.ReadyToUpload
                )
            val storedSample = repository.store(sample)

            remoteStorage.enqueue(storedSample, context)
            view.finishFlow()
        }
        catch(@Suppress("TooGenericExceptionCaught") error: Throwable) {
            Timber.e(error, "An error occurred when saving sample")
            view.showRetryBar()
        }
    }

    suspend fun addImage() {
        val current = repository.current()
        view.captureImage(current.nextImageName())
    }

    suspend fun editImage(capture: CompletedCapture) {
        val current = repository.current()
        view.editCapture(current.disease, capture)
    }
}
