package net.aiscope.gdd_app.ui.metadata

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_metadata.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.goToHome
import javax.inject.Inject

class MetadataActivity : AppCompatActivity() , MetadataView {

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

        presenter.showScreen()

        metadata_save_sample.setOnClickListener {
            presenter.save(
                when (metadata_section_smear_type_radio_group.checkedRadioButtonId) {
                    R.id.metadata_blood_smear_thick -> SmearType.THICK
                    R.id.metadata_blood_smear_thin -> SmearType.THIN
                    else -> throw IllegalStateException(
                        "${metadata_section_smear_type_radio_group.checkedRadioButtonId} radio button id is unknown")
                }, metadata_species_spinner.selectedItem.toString(),
                metadata_stage_spinner.selectedItem.toString()
             )
        }
    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    override fun fillForm(model: ViewStateModel) {
        imagesAdapter.setImages(model.images)
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
        presenter.addImage()
    }

    private fun fillSpecies() {
        val spinner : Spinner = findViewById(R.id.metadata_species_spinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.metadata_species,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.setAdapter(adapter)
        }


    }

    private fun setSpeciesVisibility(visibility: Int) {
        val spinner : Spinner = findViewById(R.id.metadata_species_spinner)
        spinner.visibility = visibility

        val textView : TextView = findViewById(R.id.metadata_species_title)
        textView.visibility = visibility

        val divider : View = findViewById(R.id.metadata_species_divider)
        divider.visibility = visibility
    }

    private fun fillStages() {
        val spinner : Spinner = findViewById(R.id.metadata_stage_spinner)

        ArrayAdapter.createFromResource(
            this,
            R.array.metadata_stages,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.setAdapter(adapter)
        }

        spinner.visibility = View.VISIBLE
    }

    private fun setStageVisibility(visibility: Int) {
        val spinner : Spinner = findViewById(R.id.metadata_stage_spinner)
        spinner.visibility = visibility

        val textView : TextView = findViewById(R.id.metadata_stages_title)
        textView.visibility = visibility
    }

}
