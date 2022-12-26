package net.aiscope.gdd_app.ui.sample_completion.quality

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.FragmentQualityBinding
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionViewModel
import net.aiscope.gdd_app.ui.sample_completion.SampleFormFragment

class QualityFragment : SampleFormFragment, Fragment(R.layout.fragment_quality) {
    companion object {
        private const val MAGNIFICATION_MIN = 0
        private const val MAGNIFICATION_MAX = 2000
    }

    private var _binding: FragmentQualityBinding? = null
    private val binding get() = _binding!!

    private val sharedVM: SampleCompletionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQualityBinding.bind(view)
        binding.microscopeQualityDamagedSwitch.isChecked = sharedVM.microscopeDamaged
        binding.microscopeQualityMagnificationInput.setText(sharedVM.microscopeMagnification.toString())
    }

    private fun validateForm(): Boolean {
        val magnificationValue = binding.microscopeQualityMagnificationInput.text
        val isMagnificationValid = try {
            val magnificationInt = magnificationValue.toString().toInt()
            magnificationInt in MAGNIFICATION_MIN..MAGNIFICATION_MAX
        } catch (e: NumberFormatException) {
            false
        }
        binding.microscopeQualityMagnificationLayout.error =
            if (isMagnificationValid) null else getString(R.string.microscope_quality_magnification_error)
        return isMagnificationValid
    }

    override fun validateAndUpdateVM(): Boolean {
        return if (validateForm()) {
            val magnificationValue = binding.microscopeQualityMagnificationInput.text
            sharedVM.microscopeMagnification = magnificationValue.toString().toInt()
            sharedVM.microscopeDamaged = binding.microscopeQualityDamagedSwitch.isChecked
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
