package net.aiscope.gdd_app.ui.metadata

import android.content.Intent
import android.os.Bundle
import android.widget.AbsSpinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
import net.aiscope.gdd_app.ui.goToHome
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
                presenter.save(
                    MetadataMapper.getSmearType(metadata_section_smear_type_radio_group.checkedRadioButtonId),
                    MetadataMapper.getSpecies(baseContext, metadata_species_spinner.selectedItem),
                    MetadataMapper.getStage(baseContext, metadata_stage_spinner.selectedItem))
            }
        }
    }

    private fun selectSpinnerValue(spinner: AbsSpinner, value: String) {
        var index = -1
        for (i in 0 until spinner.adapter.count){
            if (spinner.adapter.getItem(i) == value) {
                index = i
                break;
            }
        }
        if (index > -1)
            spinner.setSelection(index)
    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    override fun fillForm(model: ViewStateModel) {
        imagesAdapter.setImages(model.images)
        if (model.sampleMetadata != null) {
            metadata_section_smear_type_radio_group.check(MetadataMapper.getSmearTypeId(model.sampleMetadata.smearType))
            selectSpinnerValue(
                metadata_species_spinner,
                MetadataMapper.getSpeciesValue(baseContext, model.sampleMetadata.species))
            selectSpinnerValue(
                metadata_stage_spinner,
                MetadataMapper.getStageValue(baseContext, model.sampleMetadata.stage))
        }
    }

    override fun showInvalidFormError() {
        Toast.makeText(this, R.string.metadata_invalid_form, Toast.LENGTH_SHORT).show()
    }

    override fun finishFlow() {
        goToHome()
    }

    override fun captureImage(nextImageName: String, nextMaskName: String) {
        val intent = Intent(this, CaptureImageActivity::class.java)
        intent.putExtra(CaptureImageActivity.EXTRA_IMAGE_NAME, nextImageName)
        intent.putExtra(CaptureImageActivity.EXTRA_MASK_NAME, nextMaskName)
        this.startActivity(intent)
    }

    private fun onAddImageClicked() {
        coroutineScope.launch {
            presenter.addImage()
        }
    }
}
