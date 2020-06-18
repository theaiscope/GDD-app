package net.aiscope.gdd_app.ui.metadata

import android.content.Context
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SampleStatus
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import timber.log.Timber
import java.io.File
import javax.inject.Inject

data class FieldOption(val id: Long, val title: Int)
data class ViewStateModel(
    val disease: String,
    val images: List<File>,
    val options: List<FieldOption>,
    val required: Boolean = true,
    val smearTypeId: Int? = null,
    val speciesValue: String? = null
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

        if (sample.disease == null) {
            notValid()
            return
        }

        val lastMetadata = repository.last()?.metadata
        view.fillForm(
            ViewStateModel(
                sample.disease,
                sample.images.toList(),
                emptyList(),
                smearTypeId = lastMetadata?.let { metadataMapper.getSmearTypeId(it.smearType) },
                speciesValue = lastMetadata?.let { metadataMapper.getSpeciesValue(context, it.species) }
            )
        )
    }

    fun notValid() {
        view.showInvalidFormError()
    }

    suspend fun save(smearTypeId: Int, speciesValue: String) {
        try {
            val sample = repository.current()
                .copy(metadata = SampleMetadata(
                    metadataMapper.getSmearType(smearTypeId),
                    metadataMapper.getSpecies(context, speciesValue)
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

    //Soo I guess we can call this... with WHAT param exactly??
    suspend fun editImage() {
        val current = repository.current()
        //The sample has images. What I need is an index??
        val image = current.images
        val mask = current.masks
        //TODO: get the right index
        view.editImage(current.disease!!, image.elementAt(0), mask.elementAt(0))
    }
}
