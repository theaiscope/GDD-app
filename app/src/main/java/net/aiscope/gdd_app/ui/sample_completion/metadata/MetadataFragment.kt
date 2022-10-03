package net.aiscope.gdd_app.ui.sample_completion.metadata

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.FragmentMetadataBinding
import net.aiscope.gdd_app.extensions.select
import net.aiscope.gdd_app.model.CompletedCapture
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.mask.MaskActivity
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionViewModel
import net.aiscope.gdd_app.ui.sample_completion.SampleFormFragment

class MetadataFragment : SampleFormFragment, Fragment(R.layout.fragment_metadata) {
    private var _binding: FragmentMetadataBinding? = null
    private val binding get() = _binding!!
    private val sharedVM: SampleCompletionViewModel by activityViewModels()

    private val imagesAdapter =
        SampleImagesAdapter(lifecycleScope, this::onAddImageClicked, this::onImageClicked)

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            with(sharedVM) {
                getSamples()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMetadataBinding.bind(view)
        with(binding) {
            metadataBloodSampleImages.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(
                        view.context,
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                adapter = imagesAdapter
            }



            with(sharedVM) {
                samples.observe(viewLifecycleOwner) { sample ->
                    setUpCaptures(sample)
                }

                smearTypeId?.let { metadataSectionSmearTypeRadioGroup.check(it) }
                speciesValue?.let { metadataSpeciesSpinner.select(it) }
            }
        }
    }

    override fun validateAndUpdateVM(): Boolean {
        with(binding) {
            sharedVM.smearTypeId = metadataSectionSmearTypeRadioGroup.checkedRadioButtonId
            sharedVM.speciesValue = metadataSpeciesSpinner.selectedItem.toString()
            sharedVM.comments = metadataCommentsInput.text.toString()
        }
        //No validation on this tab
        return true
    }

    private fun onAddImageClicked() {
        lifecycleScope.launch {
            val intent = Intent(context, CaptureImageActivity::class.java)
            intent.putExtra(
                CaptureImageActivity.EXTRA_IMAGE_NAME,
                sharedVM.getCurrentSample().nextImageName()
            )
            intent.putExtra(CaptureImageActivity.CAPTURE_IMAGE_FROM, METADATA_CLASS_NAME)
            resultLauncher.launch(intent)
        }
    }

    private fun onImageClicked(capture: CompletedCapture) {
        lifecycleScope.launch {
            val intent = Intent(context, MaskActivity::class.java)
            intent.putExtra(MaskActivity.EXTRA_DISEASE_NAME, sharedVM.disease)
            intent.putExtra(MaskActivity.EXTRA_IMAGE_NAME, capture.image.absolutePath)

            //Remove file extension
            val maskName = capture.mask.name.removeSuffix(".png")
            intent.putExtra(MaskActivity.EXTRA_MASK_NAME, maskName)
            intent.putExtra(MaskActivity.EXTRA_MASK_PATH, capture.mask.path)
            intent.putExtra(MaskActivity.EXTRA_MASK_FROM, METADATA_CLASS_NAME)
            resultLauncher.launch(intent)
        }
    }

    private fun setUpCaptures(sample: Sample) {
        imagesAdapter.setCaptures(sample.captures.completedCaptures)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val METADATA_CLASS_NAME = "MetadataFragment"
    }
}
