package net.aiscope.gdd_app.ui.sample_completion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.FragmentPreparationBinding
import net.aiscope.gdd_app.extensions.select

//TODO: this now misses the new 'bloodquality fields'
class PreparationFragment : Fragment(R.layout.fragment_preparation) {
    private var _binding: FragmentPreparationBinding? = null
    private val binding get() = _binding!!
    private val sharedVM: SampleCompletionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPreparationBinding.bind(view)
        with(binding) {
            with(sharedVM) {
                samplePreparationWaterTypeSpinner.select(waterType)
                samplePreparationGiemsaSwitch.isChecked = usesGiemsa
                samplePreparationGiemsaFpSwitch.isChecked = giemsaFP
                samplePreparationPbsSwitch.isChecked = usesPbs
                samplePreparationSlidesReuseSwitch.isChecked = reusesSlides
            }
        }
    }

    private fun validateForm(): Boolean {
        val isWaterTypeValid = binding.samplePreparationWaterTypeSpinner.selectedItem.toString() !=
                getString(R.string.spinner_empty_option)
        binding.samplePreparationWaterTypeError.visibility =
            if (isWaterTypeValid) View.GONE else View.VISIBLE
        return isWaterTypeValid
    }

    fun validateAndUpdateVM(): Boolean {
        return if (validateForm()) {
            with(binding) {
                sharedVM.waterType = samplePreparationWaterTypeSpinner.selectedItem.toString()
                sharedVM.usesGiemsa = samplePreparationGiemsaSwitch.isChecked
                sharedVM.giemsaFP = samplePreparationGiemsaFpSwitch.isChecked
                sharedVM.usesPbs = samplePreparationPbsSwitch.isChecked
                sharedVM.reusesSlides = samplePreparationSlidesReuseSwitch.isChecked
            }
            true
        } else {
            false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
