package net.aiscope.gdd_app.ui.metadata

import android.content.Intent
import android.os.Bundle
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
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.MalariaStage
import net.aiscope.gdd_app.model.SmearType
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
                presenter.save(selectedSmearType(), selectedSpecies(), selectedStage())
            }
        }
    }

    private fun selectedSmearType(): SmearType {
        return when (metadata_section_smear_type_radio_group.checkedRadioButtonId) {
            R.id.metadata_blood_smear_thick -> SmearType.THICK
            R.id.metadata_blood_smear_thin -> SmearType.THIN
            else -> throw IllegalStateException(
                "${metadata_section_smear_type_radio_group.checkedRadioButtonId} radio button id is unknown"
            )
        }
    }

    private fun selectedSpecies(): MalariaSpecies {
        return when (metadata_species_spinner.selectedItem) {
            getString(R.string.malaria_species_p_falciparum) -> MalariaSpecies.P_FALCIPARUM
            getString(R.string.malaria_species_p_vivax) -> MalariaSpecies.P_VIVAX
            getString(R.string.malaria_species_p_ovale) -> MalariaSpecies.P_OVALE
            getString(R.string.malaria_species_p_malariae) -> MalariaSpecies.P_MALARIAE
            getString(R.string.malaria_species_p_knowlesi) -> MalariaSpecies.P_KNOWLESI
            else -> throw IllegalStateException(
                "${metadata_species_spinner.selectedItem} species is unknown"
            )
        }
    }

    private fun selectedStage(): MalariaStage {
        return when (metadata_stage_spinner.selectedItem) {
            getString(R.string.malaria_stage_ring) -> MalariaStage.RING
            getString(R.string.malaria_stage_trophozoite) -> MalariaStage.TROPHOZOITE
            getString(R.string.malaria_stage_schizont) -> MalariaStage.SCHIZONT
            getString(R.string.malaria_stage_gametocyte) -> MalariaStage.GAMETOCYTE
            else -> throw IllegalStateException(
                "${metadata_species_spinner.selectedItem} stage is unknown"
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
        coroutineScope.launch {
            presenter.addImage()
        }
    }
}
