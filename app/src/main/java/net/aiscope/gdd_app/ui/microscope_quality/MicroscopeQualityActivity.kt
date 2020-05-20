package net.aiscope.gdd_app.ui.microscope_quality

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_microscope_quality.microscope_quality_continue_button
import kotlinx.android.synthetic.main.activity_microscope_quality.microscope_quality_damaged_switch
import kotlinx.android.synthetic.main.activity_microscope_quality.microscope_quality_magnification_edit_text
import kotlinx.android.synthetic.main.activity_microscope_quality.microscope_quality_magnification_error
import kotlinx.android.synthetic.main.toolbar.toolbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
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
        private const val DEFAULT_DAMAGED = false
        private const val DEFAULT_MAGNIFICATION = 1000
    }

    @Inject
    lateinit var presenter: MicroscopeQualityPresenter

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_microscope_quality)

        coroutineScope.launch {
            presenter.showScreen()
        }

        setSupportActionBar(toolbar)
        attachCaptureFlowToolbar(toolbar)

        microscope_quality_continue_button.setOnClickListener {
            save()
        }
    }

    override fun fillForm(model: MicroscopeQualityViewStateModel?) {
        val isDamaged: Boolean
        val magnification: Int
        if (model == null) {
            isDamaged = DEFAULT_DAMAGED
            magnification = DEFAULT_MAGNIFICATION
        } else {
            isDamaged = model.isDamaged
            magnification = model.magnification
        }
        microscope_quality_damaged_switch.isChecked = isDamaged
        microscope_quality_magnification_edit_text.setText(magnification.toString())
    }

    private fun validateForm(): Boolean {
        val magnificationValue = microscope_quality_magnification_edit_text.text
        val isMagnificationValid = try {
            val magnificationInt = magnificationValue.toString().toInt()
            magnificationInt in MAGNIFICATION_MIN..MAGNIFICATION_MAX
        } catch (e: NumberFormatException) {
            false
        }
        microscope_quality_magnification_error.visibility =
            if (isMagnificationValid) View.GONE else View.VISIBLE
        return isMagnificationValid
    }

    private fun save() {
        if (!validateForm()) return
        coroutineScope.launch {
            val viewModel = MicroscopeQualityViewStateModel(
                microscope_quality_damaged_switch.isChecked,
                microscope_quality_magnification_edit_text.text.toString().toInt()
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