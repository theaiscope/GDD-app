package net.aiscope.gdd_app.ui.sample_completion

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.android.AndroidInjection
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ActivityCompleteSampleBinding
import net.aiscope.gdd_app.model.CompletedCapture
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.goToHomeAndConfirmSaved
import net.aiscope.gdd_app.ui.mask.MaskActivity
import net.aiscope.gdd_app.ui.metadata.SampleImagesAdapter
import net.aiscope.gdd_app.ui.sample_completion.metadata.MetadataFragment
import net.aiscope.gdd_app.ui.sample_completion.preparation.PreparationFragment
import net.aiscope.gdd_app.ui.sample_completion.quality.QualityFragment
import net.aiscope.gdd_app.ui.showConfirmExitDialog
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbar
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbarAction
import timber.log.Timber
import javax.inject.Inject

class SampleCompletionActivity : CaptureFlow, AppCompatActivity() {
    private lateinit var binding: ActivityCompleteSampleBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val sharedVM: SampleCompletionViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityCompleteSampleBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)
            setSupportActionBar(toolbarLayout.toolbar)

            tabLayout.addTab(tabLayout.newTab().setText(R.string.complete_sample_metadata_tab))
            tabLayout.addTab(tabLayout.newTab().setText(R.string.complete_sample_sampleprep_tab))
            tabLayout.addTab(tabLayout.newTab().setText(R.string.complete_sample_quality_tab))
            tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = FragmentAdapter(
                this@SampleCompletionActivity, supportFragmentManager,
                tabLayout.tabCount
            )
            viewPager.adapter = adapter
            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    //call the validate and write back to VM function
                    validateTabsAndUpdateVM();
                }

                // Anything needs doing in this case?
                override fun onTabReselected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }
            })

            completionSaveSample.setOnClickListener { save() }

        }

        //Initialize the shared viewmodel for the tabs
        sharedVM.initVM()
    }

    fun validateTabsAndUpdateVM(): Boolean {
        //So how do I get the fragments??
        with(binding) {
            val metadataFragment: MetadataFragment? =
                findFragment(0)
                        as? MetadataFragment
            val metadataOk = metadataFragment?.validateAndUpdateVM() ?: true

            val preparationFragment: PreparationFragment? =
                findFragment(1)
                        as? PreparationFragment
            val preparationOK = preparationFragment?.validateAndUpdateVM() ?: true

            val qualityFragment: QualityFragment? =
                findFragment(2)
                        as? QualityFragment

            val qualityOK = qualityFragment?.validateAndUpdateVM() ?: true

            //Saving should only happen if all tabs are OK
            return (metadataOk && preparationOK && qualityOK)
        }
    }

    fun save() {
        val validationOK = validateTabsAndUpdateVM();
        if (validationOK) {
            try {
                sharedVM.save()
                finishFlow()
            } catch (@Suppress("TooGenericExceptionCaught") error: Throwable) {
                Timber.e(error, "An error occurred when saving sample preparation")
                showRetryBar()
            }
        } else {
            //TODO: So in this case we should show the tab where the error occurs?
        }
    }

    //FIXME: Pretty hacky retrieval of fragments based on the
    //Internals of FragmentPagerAdapter
    private fun ActivityCompleteSampleBinding.findFragment(index: Int) =
        supportFragmentManager.findFragmentByTag(makeFragmentName(viewPager.id, index))

    private fun makeFragmentName(viewId: Int, id: Int): String? {
        return "android:switcher:$viewId:$id"
    }


    fun showRetryBar() {
        CustomSnackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.microscope_quality_snackbar_error),
            Snackbar.LENGTH_INDEFINITE, null,
            CustomSnackbarAction(
                getString(R.string.microscope_quality_snackbar_retry)
            ) {
                sharedVM.save()
            }
        ).show()
    }

    private fun finishFlow() {
        goToHomeAndConfirmSaved()
    }

    override fun onBackPressed() {
        showConfirmExitDialog()
    }

    fun captureImage(nextImageName: String) {
        val intent = Intent(this, CaptureImageActivity::class.java)
        intent.putExtra(CaptureImageActivity.EXTRA_IMAGE_NAME, nextImageName)
        this.startActivity(intent)
    }

    fun editCapture(disease: String, capture: CompletedCapture){
        val intent = Intent(this, MaskActivity::class.java)
        intent.putExtra(MaskActivity.EXTRA_DISEASE_NAME, disease)
        intent.putExtra(MaskActivity.EXTRA_IMAGE_NAME, capture.image.absolutePath)

        //Remove file extension
        val maskName = capture.mask.name.removeSuffix(".png")
        intent.putExtra(MaskActivity.EXTRA_MASK_NAME, maskName)
        intent.putExtra(MaskActivity.EXTRA_MASK_PATH, capture.mask.path)

        startActivity(intent)
    }


}
