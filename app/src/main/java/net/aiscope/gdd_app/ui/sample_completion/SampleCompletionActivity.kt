package net.aiscope.gdd_app.ui.sample_completion

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.android.AndroidInjection
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ActivityCompleteSampleBinding
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.goToHomeAndConfirmSaved
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
    private lateinit var viewPager: ViewPager2

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
            attachCaptureFlowToolbar(toolbarLayout.toolbar)

            tabLayout.tabGravity = TabLayout.GRAVITY_FILL

            viewPager.adapter = FragmentAdapter(
                this@SampleCompletionActivity
            )

            TabLayoutMediator(tabLayout, viewPager) { tab, position ->
                tab.text = when (position) {
                    0 -> {getString(R.string.complete_sample_metadata_tab)}
                    1 -> {getString(R.string.complete_sample_sampleprep_tab)}
                    2 -> {getString(R.string.complete_sample_quality_tab)}
                    else -> {getString(R.string.complete_sample_metadata_tab)}
                }
            }.attach()

            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                    //call the validate and write back to VM function
                    validateTabsAndUpdateVM();
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }
            })

            completionSaveSample.setOnClickListener { save() }

        }

        //Initialize the shared viewmodel for the tabs
        sharedVM.initVM()
    }

    // The int indicates which tab has errors so we can switch to that one
    fun validateTabsAndUpdateVM(): Int? {
        with(binding) {
            val metadataFragment: MetadataFragment? =
                findFragment(0) as? MetadataFragment
            val metadataOk = metadataFragment?.validateAndUpdateVM() ?: true

            val preparationFragment: PreparationFragment? =
                findFragment(1) as? PreparationFragment
            val preparationOK = preparationFragment?.validateAndUpdateVM() ?: true

            val qualityFragment: QualityFragment? =
                findFragment(2) as? QualityFragment
            val qualityOK = qualityFragment?.validateAndUpdateVM() ?: true

            //Decide which (if any) tab has errors and should be displayed
            var erroneousTab: Int? = null
            if (!metadataOk) {
                erroneousTab = 0
            } else if (!preparationOK) {
                erroneousTab =  1
            } else if (!qualityOK) {
                erroneousTab = 2
            }
            return erroneousTab
        }
    }

    fun save() {
        val erroneousTab = validateTabsAndUpdateVM();
        if (erroneousTab == null) {
            try {
                sharedVM.save()
                finishFlow()
            } catch (@Suppress("TooGenericExceptionCaught") error: Throwable) {
                Timber.e(error, "An error occurred when saving sample completion data")
                showRetryBar()
            }
        } else {
            setActiveTab(erroneousTab)
        }
    }

    private fun setActiveTab(index: Int) {
        with(binding) {
            val tab = tabLayout.getTabAt(index)
            tab?.select()
        }
    }

    private fun findFragment(index: Int) =
        supportFragmentManager.findFragmentByTag("f$index")

    private fun showRetryBar() {
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
}
