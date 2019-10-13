package net.aiscope.gdd_app.ui.metadata

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_metadata.*
import kotlinx.android.synthetic.main.toolbar.*
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.goToHome
import javax.inject.Inject

class MetadataActivity : AppCompatActivity() , MetadataView {

    @Inject lateinit var presenter: MetadataPresenter

    private val imagesAdapter = SampleImagesAdapter(this::onAddImageClicked)

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
                when(metadata_section_smear_type_radio_group.checkedRadioButtonId) {
                    R.id.metadata_blood_smear_thick -> SmearType.THICK
                    R.id.metadata_blood_smear_thin -> SmearType.THIN
                    else -> throw IllegalStateException("${metadata_section_smear_type_radio_group.checkedRadioButtonId} radio button id is unknown")
                }
             )
        }
    }

    override fun fillForm(model: ViewStateModel) {
        imagesAdapter.setImages(model.images)
        // TODO set species stages
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

}
