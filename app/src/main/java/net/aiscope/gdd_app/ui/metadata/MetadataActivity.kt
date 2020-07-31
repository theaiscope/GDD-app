package net.aiscope.gdd_app.ui.metadata

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ActivityMetadataBinding
import net.aiscope.gdd_app.extensions.select
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.goToHomeAndConfirmSaved
import net.aiscope.gdd_app.ui.mask.MaskActivity
import net.aiscope.gdd_app.ui.showConfirmExitDialog
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbar
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbarAction
import java.io.File
import javax.inject.Inject

@Suppress("TooManyFunctions")
class MetadataActivity : AppCompatActivity(), MetadataView, CaptureFlow {

    @Inject
    lateinit var presenter: MetadataPresenter

    private val imagesAdapter =
        SampleImagesAdapter(lifecycleScope, this::onAddImageClicked, this::onImageClicked)

    private lateinit var binding: ActivityMetadataBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityMetadataBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)
            setSupportActionBar(toolbarLayout.toolbar)
            attachCaptureFlowToolbar(toolbarLayout.toolbar)

            metadataBloodSampleImages.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(
                        this@MetadataActivity,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                adapter = imagesAdapter
            }

            lifecycleScope.launch {
                presenter.showScreen()
            }

            metadataSaveSample.setOnClickListener {
                lifecycleScope.launch {
                    save()
                }
            }
        }
    }

    override fun fillForm(model: ViewStateModel) {
        imagesAdapter.setImages(model.images)
        imagesAdapter.setMasks(model.masks)
        model.smearTypeId?.let { binding.metadataSectionSmearTypeRadioGroup.check(it) }
        model.speciesValue?.let { binding.metadataSpeciesSpinner.select(it) }
    }

    override fun showInvalidFormError() {
        Toast.makeText(this, R.string.metadata_invalid_form, Toast.LENGTH_SHORT).show()
    }

    override fun showRetryBar() {
        CustomSnackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.metadata_snackbar_error),
            Snackbar.LENGTH_INDEFINITE, null,
            CustomSnackbarAction(getString(R.string.metadata_snackbar_retry), View.OnClickListener {
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

    override fun editImage(disease: String, image: File, mask: File){
        val intent = Intent(this, MaskActivity::class.java)
        intent.putExtra(MaskActivity.EXTRA_DISEASE_NAME, disease)
        intent.putExtra(MaskActivity.EXTRA_IMAGE_NAME, image.absolutePath)

        //Remove file extension
        val maskName = mask.name.removeSuffix(".png")
        intent.putExtra(MaskActivity.EXTRA_MASK_NAME, maskName)
        intent.putExtra(MaskActivity.EXTRA_MASK_PATH, mask.path)

        startActivity(intent)
    }
    
    override fun onBackPressed() {
        showConfirmExitDialog()
    }

    private fun save() {
        lifecycleScope.launch {
            presenter.save(
                binding.metadataSectionSmearTypeRadioGroup.checkedRadioButtonId,
                binding.metadataSpeciesSpinner.selectedItem.toString()
            )
        }
    }

    private fun onAddImageClicked() {
        lifecycleScope.launch {
            presenter.addImage()
        }
    }

    private fun onImageClicked(image: File, mask: File) {
        lifecycleScope.launch {
            presenter.editImage(image, mask)
        }
    }

}
