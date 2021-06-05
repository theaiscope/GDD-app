package net.aiscope.gdd_app.ui.sample_completion

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
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
            if (!metadataOk) {
                return 0
            }

            val preparationFragment: PreparationFragment? =
                findFragment(1) as? PreparationFragment
            val preparationOK = preparationFragment?.validateAndUpdateVM() ?: true
            if (!preparationOK) {
                return 1
            }

            val qualityFragment: QualityFragment? =
                findFragment(2) as? QualityFragment
            val qualityOK = qualityFragment?.validateAndUpdateVM() ?: true
            if (!qualityOK) {
                return 2
            }

            return null
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

    //Improvement: Pretty hacky retrieval of fragments based on the
    //Internals of FragmentPagerAdapter
    private fun ActivityCompleteSampleBinding.findFragment(index: Int) =
        supportFragmentManager.findFragmentByTag(makeFragmentName(viewPager.id, index))

    private fun makeFragmentName(viewId: Int, id: Int): String? {
        return "android:switcher:$viewId:$id"
    }


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
