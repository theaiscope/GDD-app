package net.aiscope.gdd_app.ui.sample_completion

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.BloodQuality
import net.aiscope.gdd_app.model.CompletedCapture
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.model.SampleStatus
import net.aiscope.gdd_app.model.WaterType
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.metadata.FieldOption
import net.aiscope.gdd_app.ui.metadata.MetadataMapper
import timber.log.Timber
import javax.inject.Inject

class SampleCompletionViewModel @Inject constructor(
    private val repository: SampleRepository,
    private val remoteStorage: RemoteStorage,
    private val context: Context,
) : ViewModel() {
    //TODO: We got a nicer way of fixing default vals??

    private val _currentDisease =
        MutableLiveData<Sample>()
    val currentDisease: LiveData<Sample>
        get() = _currentDisease

    // Fields for the metadata tab
    var disease: String = ""
    var captures: List<CompletedCapture> = emptyList()
    var options: List<FieldOption> = emptyList()
    var required: Boolean = true
    var smearTypeId: Int? = null
    var speciesValue: String? = null
    var comments: String? = null

    // Fields for preparation tab. Perhaps we should nest this?
    var waterType: String = context.getString(R.string.spinner_empty_option)
    var usesGiemsa: Boolean = true
    var giemsaFP: Boolean = true
    var usesPbs: Boolean = true
    var reusesSlides: Boolean = false
    var bloodQuality: String = context.getString(R.string.spinner_empty_option)

    //Fields for the microscope tab
    var microscopeDamaged: Boolean = false
    var microscopeMagnification: Int = 1000


    override fun toString(): String {
        val builder = StringBuilder()
        this.javaClass.declaredFields.forEach {
            builder.append(
                "* " + it.name + " : " + this.javaClass.getDeclaredField(it.name).get(this) + "\n"
            )
        }
        return builder.toString()
    }

    fun initVM() {
        //TODO: inject dispatchers
        viewModelScope.launch(Dispatchers.IO) {

            val sample = repository.current()
            _currentDisease.postValue(sample)
            disease = sample.disease
            captures = sample.captures.completedCaptures

            //So what we gonna do here for the meta stuff?
            val lastMeta = repository.lastSaved()?.metadata
            Timber.i("LAST META %s", lastMeta)
            smearTypeId = MetadataMapper.getSmearTypeId(lastMeta?.smearType!!)
            speciesValue = MetadataMapper.getSpeciesValue(context, lastMeta?.species!!)

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
            bloodQuality = getBloodQualityValue(lastPreparation?.bloodQuality)
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
                reusesSlides,
                getBloodQuality(bloodQuality)
            )

            //Aight so what else for the meta stuff?
            val newMeta = SampleMetadata(
                //TODO: move these methods out of MDMapper
                smearType = MetadataMapper.getSmearType(smearTypeId!!),
                species = MetadataMapper.getSpecies(context, speciesValue!!),
                comments = comments ?: ""
            )

            val updatedSample = repository.current().copy(
                microscopeQuality = newQualityValues,
                preparation = newPreparation,
                metadata = newMeta,
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

    private fun getBloodQuality(bloodQualityValue: String): BloodQuality {
        return when (bloodQualityValue) {
            context.getString(R.string.blood_quality_fresh) -> BloodQuality.FRESH
            context.getString(R.string.blood_quality_old) -> BloodQuality.OLD
            else -> throw IllegalStateException("$bloodQualityValue blood type is unknown")
        }
    }

    private fun getBloodQualityValue(bloodQuality: BloodQuality?): String {
        return when (bloodQuality) {
            BloodQuality.FRESH -> context.getString(R.string.blood_quality_fresh)
            BloodQuality.OLD -> context.getString(R.string.blood_quality_old)
            null -> ""
        }
    }
}
