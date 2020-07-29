package net.aiscope.gdd_app.ui.microscope_quality

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ActivityMicroscopeQualityBinding
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbar
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbarAction
import javax.inject.Inject

class MicroscopeQualityActivity : AppCompatActivity(), MicroscopeQualityView, CaptureFlow {

    companion object {
        private const val MAGNIFICATION_MIN = 0
        private const val MAGNIFICATION_MAX = 2000
        private val DEFAULT_FORM_DATA = MicroscopeQualityViewStateModel(false, 1000)
    }

    @Inject
    lateinit var presenter: MicroscopeQualityPresenter

    private lateinit var binding: ActivityMicroscopeQualityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMicroscopeQualityBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)

            setSupportActionBar(toolbar.toolbar)
            attachCaptureFlowToolbar(toolbar.toolbar)

            microscopeQualityContinueButton.setOnClickListener { save() }
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

    override fun showRetryBar() {
        CustomSnackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.microscope_quality_snackbar_error),
            Snackbar.LENGTH_INDEFINITE, null,
            CustomSnackbarAction(
                getString(R.string.microscope_quality_snackbar_retry),
                View.OnClickListener {
                    save()
                })
        ).show()
    }

    override fun goToCaptureImage(nextImageName: String) {
        val intent = Intent(this, CaptureImageActivity::class.java)
        intent.putExtra(CaptureImageActivity.EXTRA_IMAGE_NAME, nextImageName)
        this.startActivity(intent)
    }
}
