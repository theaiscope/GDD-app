package net.aiscope.gdd_app.ui.sample_completion

import androidx.lifecycle.ViewModel
import net.aiscope.gdd_app.repository.SampleRepository
import javax.inject.Inject

class SampleCompletionViewModel @Inject constructor(
    private val repository: SampleRepository,
    ): ViewModel() {
    var microscopeDamaged: Boolean = false
    var microscopeMagnification: Int = 1000

    suspend fun initVM(){
        val lastMicroscopeQuality = repository.lastSaved()?.microscopeQuality
        microscopeDamaged = lastMicroscopeQuality?.isDamaged ?: false
        microscopeMagnification = lastMicroscopeQuality?.magnification ?: 1000
    }

}