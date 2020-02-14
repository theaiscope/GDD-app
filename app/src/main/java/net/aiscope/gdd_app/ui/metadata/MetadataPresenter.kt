package net.aiscope.gdd_app.ui.metadata

import android.content.Context
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.MalariaStage
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.model.Status
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import java.io.File
import javax.inject.Inject

data class FieldOption(val id: Long, val title: Int)
data class ViewStateModel(
    val disease: String,
    val images: List<File>,
    val options: List<FieldOption>,
    val required: Boolean = true
)

class MetadataPresenter @Inject constructor(
    private val view: MetadataView,
    private val repository: SampleRepository,
    private val remoteStorage: RemoteStorage,
    private val context: Context
) {

    suspend fun showScreen() {
        val sample = repository.current()

        if (sample.disease == null) {
            notValid()
            return
        }

        view.fillForm(ViewStateModel(sample.disease, sample.images.toList(), emptyList()))
    }

    fun notValid() {
        view.showInvalidFormError()
    }

    suspend fun save(smearType: SmearType, species: MalariaSpecies, stage: MalariaStage) {
        val sample = repository.current()
            .copy(metadata = SampleMetadata(smearType, species, stage), status = Status.ReadyToUpload)
        repository.store(sample)

        remoteStorage.enqueue(sample, context)

        view.finishFlow()
    }

    suspend fun addImage() {
        val current = repository.current()
        view.captureImage(current.nextImageName(), current.nextMaskName())
    }
}
