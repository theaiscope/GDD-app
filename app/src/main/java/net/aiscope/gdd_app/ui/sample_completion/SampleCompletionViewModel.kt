package net.aiscope.gdd_app.ui.sample_completion

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.model.SampleStatus
import net.aiscope.gdd_app.model.WaterType
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import timber.log.Timber
import javax.inject.Inject

class SampleCompletionViewModel @Inject constructor(
    private val repository: SampleRepository,
    private val remoteStorage: RemoteStorage,
    private val context: Context,
) : ViewModel() {


    //TODO: We got a nicer way of fixing default vals??
    var microscopeDamaged: Boolean = false
    var microscopeMagnification: Int = 1000

    // Fields for preparation tab. Perhaps we should nest this?
    var waterType = context.getString(R.string.spinner_empty_option)
    var usesGiemsa = true
    var giemsaFP = true
    var usesPbs = true
    var reusesSlides = false

    fun initVM() {
        //TODO: inject dispatchers
        viewModelScope.launch(Dispatchers.IO) {
            val lastMicroscopeQuality = repository.lastSaved()?.microscopeQuality
            Timber.i("Last micro: %s", lastMicroscopeQuality)
            microscopeDamaged = lastMicroscopeQuality?.isDamaged ?: false
            microscopeMagnification = lastMicroscopeQuality?.magnification ?: 1000

            //Set the values for the prep same as the last one
            val lastPreparation = repository.lastSaved()?.preparation
            Timber.i("Last prep %s", lastPreparation)

            waterType = getWaterTypeValue(lastPreparation?.waterType)
            usesGiemsa = lastPreparation?.usesGiemsa ?: true
            giemsaFP = lastPreparation?.giemsaFP ?: true
            usesPbs = lastPreparation?.usesPbs ?: true
            reusesSlides = lastPreparation?.reusesSlides ?: false
        }
    }

    fun save() {
        //TODO: inject dispatchers
        viewModelScope.launch(Dispatchers.IO) {
            val newQualityValues = MicroscopeQuality(microscopeDamaged, microscopeMagnification)
            val newPreparation = SamplePreparation(
                getWaterType(waterType),
                usesGiemsa,
                giemsaFP,
                usesPbs,
                reusesSlides
            )

            val updatedSample = repository.current().copy(
                microscopeQuality = newQualityValues,
                preparation = newPreparation,
                status = SampleStatus.ReadyToUpload
            )
            val storedSample = repository.store(updatedSample)

            //Put it in line for uploading to firebase
            remoteStorage.enqueue(storedSample, context)
        }
    }

    private fun getWaterType(waterTypeValue: String): WaterType {
        return when (waterTypeValue) {
            context.getString(R.string.water_type_distilled) -> WaterType.DISTILLED
            context.getString(R.string.water_type_bottled) -> WaterType.BOTTLED
            context.getString(R.string.water_type_tap) -> WaterType.TAP
            context.getString(R.string.water_type_well) -> WaterType.WELL
            else -> throw IllegalStateException("$waterTypeValue water type is unknown")
        }
    }

    private fun getWaterTypeValue(waterType: WaterType?): String {
        return when (waterType) {
            WaterType.DISTILLED -> context.getString(R.string.water_type_distilled)
            WaterType.BOTTLED -> context.getString(R.string.water_type_bottled)
            WaterType.TAP -> context.getString(R.string.water_type_tap)
            WaterType.WELL -> context.getString(R.string.water_type_well)
            null -> ""
        }
    }


}
