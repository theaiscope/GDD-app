package net.aiscope.gdd_app.ui.sample_completion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.FragmentQualityBinding
import timber.log.Timber

class QualityFragment : Fragment(R.layout.fragment_quality) {
    companion object {
        private const val MAGNIFICATION_MIN = 0
        private const val MAGNIFICATION_MAX = 2000
    }

    private var _binding: FragmentQualityBinding? = null
    private val binding get() = _binding!!

    //So this should back all 3 fragment 'views'
    private val sharedVM: SampleCompletionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentQualityBinding.bind(view)
        Timber.i(
            "magnification: %s dam: %s",
            sharedVM.microscopeMagnification,
            sharedVM.microscopeDamaged
        )
        binding?.apply {
            binding.microscopeQualityDamagedSwitch.isChecked = sharedVM.microscopeDamaged
            binding.microscopeQualityMagnificationInput.setText(sharedVM.microscopeMagnification.toString())
        }
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

    fun validateAndUpdateVM(): Boolean{
        return if(validateForm()){
            val magnificationValue = binding.microscopeQualityMagnificationInput.text
            sharedVM.microscopeMagnification =  magnificationValue.toString().toInt()
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
