package net.aiscope.gdd_app.ui.sample_preparation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_sample_preparation.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.extensions.select
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.microscope_quality.MicroscopeQualityActivity
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbar
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbarAction
import javax.inject.Inject

class SamplePreparationActivity : AppCompatActivity(), SamplePreparationView, CaptureFlow {

    private val defaultFormData: SamplePreparationViewStateModel by lazy {
        SamplePreparationViewStateModel(
            getString(R.string.spinner_empty_option),
            usesGiemsa = true,
            giemsaFP = true,
            usesPbs = true,
            usesAlcohol = true,
            reusesSlides = false
        )
    }

    @Inject
    lateinit var presenter: SamplePreparationPresenter

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sample_preparation)

        coroutineScope.launch {
            presenter.showScreen()
        }

        setSupportActionBar(toolbar)
        attachCaptureFlowToolbar(toolbar)

        sample_preparation_continue_button.setOnClickListener {
            save()
        }
    }

    override fun fillForm(model: SamplePreparationViewStateModel?) {
        val formData = model ?: defaultFormData
        with(formData) {
            sample_preparation_water_type_spinner.select(waterType)
            sample_preparation_giemsa_switch.isChecked = usesGiemsa
            sample_preparation_giemsa_fp_switch.isChecked = giemsaFP
            sample_preparation_pbs_switch.isChecked = usesPbs
            sample_preparation_alcohol_switch.isChecked = usesAlcohol
            sample_preparation_slides_reuse_switch.isChecked = reusesSlides
        }
    }

    private fun validateForm(): Boolean {
        val isWaterTypeValid = sample_preparation_water_type_spinner.selectedItem.toString() !=
                getString(R.string.spinner_empty_option)
        sample_preparation_water_type_error.visibility =
            if (isWaterTypeValid) View.GONE else View.VISIBLE
        return isWaterTypeValid
    }

    private fun save() {
        if (!validateForm()) return
        coroutineScope.launch {
            val viewModel = SamplePreparationViewStateModel(
                sample_preparation_water_type_spinner.selectedItem.toString(),
                sample_preparation_giemsa_switch.isChecked,
                sample_preparation_giemsa_fp_switch.isChecked,
                sample_preparation_pbs_switch.isChecked,
                sample_preparation_alcohol_switch.isChecked,
                sample_preparation_slides_reuse_switch.isChecked
            )
            presenter.save(viewModel)
        }
    }

    override fun showRetryBar() {
        CustomSnackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.sample_preparation_snackbar_error),
            Snackbar.LENGTH_INDEFINITE, null,
            CustomSnackbarAction(
                getString(R.string.sample_preparation_snackbar_retry),
                View.OnClickListener {
                    save()
                })
        ).show()
    }

    override fun goToMicroscopeQuality() {
        val intent = Intent(this, MicroscopeQualityActivity::class.java)
        this.startActivity(intent)
    }
}
