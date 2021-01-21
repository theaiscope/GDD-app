package net.aiscope.gdd_app.ui.sample_completion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ActivityMicroscopeQualityBinding
import net.aiscope.gdd_app.databinding.FragmentQualityBinding
import net.aiscope.gdd_app.ui.microscope_quality.MicroscopeQualityPresenter
import net.aiscope.gdd_app.ui.microscope_quality.MicroscopeQualityViewStateModel
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbar
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbarAction
import javax.inject.Inject

class QualityFragment : QualityView, Fragment() {
    companion object {
        private const val MAGNIFICATION_MIN = 0
        private const val MAGNIFICATION_MAX = 2000
        private val DEFAULT_FORM_DATA = MicroscopeQualityViewStateModel(false, 1000)
    }

    @Inject
    lateinit var presenter: MicroscopeQualityPresenter

    private lateinit var binding: FragmentQualityBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quality, container, false)

        binding = FragmentQualityBinding.inflate(layoutInflater)
        with(binding) {
            //FIXME: So quite a lot of these should be done in the activity, outside of the tabs...
//            setContentView(root)
//
//            setSupportActionBar(toolbarLayout.toolbar)
//            attachCaptureFlowToolbar(toolbarLayout.toolbar)

//            microscopeQualityContinueButton.setOnClickListener { save() }
        }

        lifecycleScope.launch {
            presenter.showScreen()
        }

    }

    override fun fillForm(model: MicroscopeQualityViewStateModel?) {
        val formData = model ?: DEFAULT_FORM_DATA
        binding.microscopeQualityDamagedSwitch.isChecked = formData.isDamaged
        binding.microscopeQualityMagnificationInput.setText(formData.magnification.toString())
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

    private fun save() {
        if (!validateForm()) return
        lifecycleScope.launch {
            val viewModel = MicroscopeQualityViewStateModel(
                binding.microscopeQualityDamagedSwitch.isChecked,
                binding.microscopeQualityMagnificationInput.text.toString().toInt()
            )
            presenter.save(viewModel)
        }
    }

    //TODO: what's this do?
    //Probs should be done by activity instead
//    override fun showRetryBar() {
//        CustomSnackbar.make(
//            findViewById(android.R.id.content),
//            getString(R.string.microscope_quality_snackbar_error),
//            Snackbar.LENGTH_INDEFINITE, null,
//            CustomSnackbarAction(
//                getString(R.string.microscope_quality_snackbar_retry),
//                View.OnClickListener {
//                    save()
//                })
//        ).show()
//    }

}