package net.aiscope.gdd_app.ui.sample_completion

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.FragmentMetadataBinding
import net.aiscope.gdd_app.extensions.select
import net.aiscope.gdd_app.model.CompletedCapture
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.ui.metadata.SampleImagesAdapter
import timber.log.Timber

class MetadataFragment : Fragment(R.layout.fragment_metadata) {
    private var _binding: FragmentMetadataBinding? = null
    private val binding get() = _binding!!
    private val sharedVM: SampleCompletionViewModel by activityViewModels()

    private val imagesAdapter =
        SampleImagesAdapter(lifecycleScope, this::onAddImageClicked, this::onImageClicked)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMetadataBinding.bind(view)
        initAdapter()
        sharedVM.currentDisease.observe(viewLifecycleOwner, { current-> current?.let { initView(it) } })
    }

    private fun initAdapter() {
        with(binding) {
            metadataBloodSampleImages.apply {
                setHasFixedSize(true)
                layoutManager =
                    LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                    )
                adapter = imagesAdapter
            }
        }
    }

    private fun initView(current: Sample) {
        with(binding) {
            with(sharedVM) {
                Timber.i("VM: %s", sharedVM)
                imagesAdapter.setCaptures(current.captures.completedCaptures)

                //Does not seem to initialize correctly??
                smearTypeId?.let { metadataSectionSmearTypeRadioGroup.check(it) }
                speciesValue?.let { metadataSpeciesSpinner.select(it) }
                //Comments?
            }
        }
    }

    private fun validateForm(): Boolean {
        return true
    }

    fun validateAndUpdateVM(): Boolean {
        return if (validateForm()) {
            with(binding) {
                sharedVM.smearTypeId = metadataSectionSmearTypeRadioGroup.checkedRadioButtonId
                sharedVM.speciesValue = metadataSpeciesSpinner.selectedItem.toString()
                sharedVM.comments = metadataCommentsInput.text.toString()
            }
            true
        } else {
            false
        }
    }

    private fun onAddImageClicked() {
        val current = sharedVM.currentDisease.value
        if(activity is SampleCompletionActivity) {
            current?.let {
                (activity as SampleCompletionActivity).captureImage(it.nextImageName())
            }
        }
    }

    private fun onImageClicked(capture: CompletedCapture) {
        val current = sharedVM.currentDisease.value
        if(activity is SampleCompletionActivity) {
                current?.let {
                    (activity as SampleCompletionActivity).editCapture(it.disease, capture)
                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}