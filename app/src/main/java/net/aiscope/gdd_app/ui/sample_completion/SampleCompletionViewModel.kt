package net.aiscope.gdd_app.ui.sample_completion

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.CompletedCapture
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleAge
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.model.SampleStatus
import net.aiscope.gdd_app.model.WaterType
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.MicroscopistRepository
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.repository.SampleRepositoryFirestore
import net.aiscope.gdd_app.ui.sample_completion.metadata.MetadataMapper
import javax.inject.Inject

@Suppress("TooManyFunctions")
class SampleCompletionViewModel @Inject constructor(
    private val repository: SampleRepository,
    private val remoteStorage: RemoteStorage,
    private val context: Context,
    private val microscopistRepository: MicroscopistRepository,
    private val sampleRepositoryFirestore: SampleRepositoryFirestore
) : ViewModel() {
    companion object {
        const val DEFAULT_MAGNIFICATION: Int = 1000
    }

    private val _samples = MutableLiveData<Sample>()
    val samples: LiveData<Sample>
        get() = _samples

    // Improvement: We got a nicer way of fixing default vals??
    // Fields for the metadata tab
    var disease: String = ""
    var captures: List<CompletedCapture> = emptyList()
    var smearTypeId: Int? = null
    var speciesValue: String? = null
    var comments: String? = null

    // Fields for preparation tab. Perhaps we should nest this?
    var waterType: String = context.getString(R.string.spinner_empty_option)
    var usesGiemsa: Boolean = true
    var giemsaFP: Boolean = true
    var usesPbs: Boolean = true
    var reusesSlides: Boolean = false
    var sampleAge: String = context.getString(R.string.spinner_empty_option)

    //Fields for the microscope tab
    var microscopeDamaged: Boolean = false
    var microscopeMagnification: Int = DEFAULT_MAGNIFICATION


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
        //Improvement: inject dispatchers here
        viewModelScope.launch(Dispatchers.IO) {

            val sample = repository.current()
            _samples.postValue(sample)
            disease = sample.disease
            captures = sample.captures.completedCaptures

            val lastSaved = repository.lastSaved()

            val lastMeta = lastSaved?.metadata
            lastMeta?.let {
                smearTypeId = MetadataMapper.getSmearTypeId(it.smearType)
                speciesValue = MetadataMapper.getSpeciesValue(context, it.species)
            }

            val lastMicroscopeQuality = lastSaved?.microscopeQuality
            lastMicroscopeQuality?.let {
                microscopeDamaged = it.isDamaged
                microscopeMagnification = it.magnification
            }

            val lastPreparation = lastSaved?.preparation
            lastPreparation?.let {
                waterType = getWaterTypeValue(it.waterType)
                usesGiemsa = it.usesGiemsa
                giemsaFP = it.giemsaFP
                usesPbs = it.usesPbs
                reusesSlides = it.reusesSlides
                sampleAge = getSampleAgeValue(it.sampleAge)
            }
        }
    }

    fun getSamples() {
        viewModelScope.launch(Dispatchers.IO) {
            val sample = repository.current()
            _samples.postValue(sample)
        }
    }

    fun save() {
        //Improvement: inject dispatchers here
        viewModelScope.launch(Dispatchers.IO) {
            val newQualityValues = MicroscopeQuality(microscopeDamaged, microscopeMagnification)
            val newPreparation = SamplePreparation(
                getWaterType(waterType),
                usesGiemsa,
                giemsaFP,
                usesPbs,
                reusesSlides,
                getSampleAge(sampleAge)
            )

            val newMeta = SampleMetadata(
                // Improvement: move these methods out of MDMapper?
                // Or do the opposite and move it all into mappers?
                smearType = MetadataMapper.getSmearType(smearTypeId),
                species = MetadataMapper.getSpecies(context, speciesValue),
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
            if(!hasUserSubmitSampleFirstTime())
            {
                microscopistRepository.store(
                    microscopistRepository.load().copy(hasSubmitSampleFirstTime = true)
                )
            }
            sampleRepositoryFirestore.store(storedSample)
        }
    }

    fun hasUserSubmitSampleFirstTime() : Boolean {
        return microscopistRepository.load().hasSubmitSampleFirstTime
    }

    private fun getWaterType(waterTypeValue: String): WaterType {
        return when (waterTypeValue) {
            context.getString(R.string.water_type_distilled) -> WaterType.DISTILLED
            context.getString(R.string.water_type_bottled) -> WaterType.BOTTLED
            context.getString(R.string.water_type_tap) -> WaterType.TAP
            context.getString(R.string.water_type_well) -> WaterType.WELL
            context.getString(R.string.water_type_unknown) -> WaterType.UNKNOWN
            else -> error("$waterTypeValue water type is not allowed")
        }
    }

    private fun getWaterTypeValue(waterType: WaterType?): String {
        return when (waterType) {
            WaterType.DISTILLED -> context.getString(R.string.water_type_distilled)
            WaterType.BOTTLED -> context.getString(R.string.water_type_bottled)
            WaterType.TAP -> context.getString(R.string.water_type_tap)
            WaterType.WELL -> context.getString(R.string.water_type_well)
            WaterType.UNKNOWN -> context.getString(R.string.water_type_unknown)
            null -> ""
        }
    }

    @Suppress("SwallowedException")
    fun isValidWaterTypeValue(waterTypeValue: String): Boolean {
        return try {
            getWaterType(waterTypeValue)
            true
        } catch (e: IllegalStateException) {
            false
        }
    }

    private fun getSampleAge(sampleAgeValue: String): SampleAge {
        return when (sampleAgeValue) {
            context.getString(R.string.sample_age_fresh) -> SampleAge.FRESH
            context.getString(R.string.sample_age_old) -> SampleAge.OLD
            else -> error("$sampleAgeValue sample age is not allowed")
        }
    }

    private fun getSampleAgeValue(sampleAge: SampleAge?): String {
        return when (sampleAge) {
            SampleAge.FRESH -> context.getString(R.string.sample_age_fresh)
            SampleAge.OLD -> context.getString(R.string.sample_age_old)
            null -> ""
        }
    }

    @Suppress("SwallowedException")
    fun isValidSampleAgeValue(sampleAgeValue: String): Boolean {
        return try{
            getSampleAge(sampleAgeValue)
            true
        } catch (e: IllegalStateException) {
            false
        }
    }

    suspend fun getCurrentSample(): Sample {
        return repository.current()
    }
}
