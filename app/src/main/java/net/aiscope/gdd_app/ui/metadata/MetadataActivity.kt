package net.aiscope.gdd_app.ui.metadata

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AbsSpinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_metadata.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.goToHomeAndConfirmSaved
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbar
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbarAction
import javax.inject.Inject

class MetadataActivity : AppCompatActivity() , MetadataView, CaptureFlow {

    @Inject lateinit var presenter: MetadataPresenter

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    private val imagesAdapter = SampleImagesAdapter(coroutineScope, this::onAddImageClicked)

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metadata)

        setSupportActionBar(toolbar)
        attachCaptureFlowToolbar(toolbar)

        metadata_blood_sample_images.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@MetadataActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = imagesAdapter
        }

        coroutineScope.launch {
            presenter.showScreen()
        }

        metadata_save_sample.setOnClickListener {
            coroutineScope.launch {
                save()
            }
        }
    }

    private fun selectSpinnerValue(spinner: AbsSpinner, value: String) {
        (0 until spinner.adapter.count)
            .firstOrNull { spinner.adapter.getItem(it) == value }
            ?.let { spinner.setSelection(it) }
    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    override fun fillForm(model: ViewStateModel) {
        imagesAdapter.setImages(model.images)
        model.smearTypeId?.let { metadata_section_smear_type_radio_group.check(it) }
        model.speciesValue?.let { selectSpinnerValue(metadata_species_spinner, it) }
    }

    override fun showInvalidFormError() {
        Toast.makeText(this, R.string.metadata_invalid_form, Toast.LENGTH_SHORT).show()
    }

    override fun showRetryBar() {
        CustomSnackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.metadata_snackbar_error),
            Snackbar.LENGTH_INDEFINITE,null,
            CustomSnackbarAction(getString(R.string.metadata_snackbar_retry),View.OnClickListener {
                save()
            })
        ).show()
    }

    override fun finishFlow() {
        goToHomeAndConfirmSaved()
    }

    override fun captureImage(nextImageName: String) {
        val intent = Intent(this, CaptureImageActivity::class.java)
        intent.putExtra(CaptureImageActivity.EXTRA_IMAGE_NAME, nextImageName)
        this.startActivity(intent)
    }

    private fun save() {
        coroutineScope.launch {
            presenter.save(
                metadata_section_smear_type_radio_group.checkedRadioButtonId,
                metadata_species_spinner.selectedItem.toString()
            )
        }
    }

    private fun onAddImageClicked() {
        coroutineScope.launch {
            presenter.addImage()
        }
    }
}
