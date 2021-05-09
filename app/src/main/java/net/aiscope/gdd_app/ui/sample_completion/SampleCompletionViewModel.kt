package net.aiscope.gdd_app.ui.sample_completion

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.SampleStatus
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

    fun initVM() {
        //TODO: inject dispatchers
        viewModelScope.launch(Dispatchers.IO) {
            val lastMicroscopeQuality = repository.lastSaved()?.microscopeQuality
            Timber.i("Last micro: %s", lastMicroscopeQuality)
            microscopeDamaged = lastMicroscopeQuality?.isDamaged ?: false
            microscopeMagnification = lastMicroscopeQuality?.magnification ?: 1000

            val lastPreparation = repository.lastSaved()?.preparation
            Timber.i("Last prep %s", lastPreparation)

            //TODO: so are we going to keep all fields on the first level or make
            //a layered structure by tab?

            //Set the values for the prep same as the last one
        }
    }

    //TODO: so this validation could be done on the fragment itself?
    //That way we can also know which fragment has something wrong still...
    //Or do we need to do a full validation of the entire VM here?
//    private fun validateForm(): Boolean {
//        val magnificationValue = sharedVM.microscopeMagnification
//        val isMagnificationValid = try {
//            val magnificationInt = magnificationValue.toString().toInt()
//            magnificationInt in MicroscopeQualityActivity.MAGNIFICATION_MIN..MicroscopeQualityActivity.MAGNIFICATION_MAX
//        } catch (e: NumberFormatException) {
//            false
//        }
//        binding.microscopeQualityMagnificationLayout.error =
//            if (isMagnificationValid) null else getString(R.string.microscope_quality_magnification_error)
//        return isMagnificationValid
//    }


    fun save() {
        //TODO: inject dispatchers
        viewModelScope.launch(Dispatchers.IO) {
            val newQualityValues = MicroscopeQuality(microscopeDamaged, microscopeMagnification)
            Timber.i("New quality values are %s", newQualityValues)

            //TODO: so before marking it ready to upload we should validate fields...
            //May be good to have a validate _per_ fragment so we can see where the
            //error is..


            //FIXME HMMM so now it uploads but not with the right values
            //OK so apparently I have to mark it 'readyToUpload' it's done
            val updatedSample = repository.current().copy(
                microscopeQuality = newQualityValues,
                status = SampleStatus.ReadyToUpload
            )
            val storedSample = repository.store(updatedSample)

            //Put it in line for uploading to firebase
            remoteStorage.enqueue(storedSample, context)
        }
    }

}
