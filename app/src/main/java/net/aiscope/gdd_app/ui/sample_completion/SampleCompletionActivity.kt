package net.aiscope.gdd_app.ui.sample_completion

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.android.AndroidInjection
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ActivityCompleteSampleBinding
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.goToHomeAndConfirmSaved
import net.aiscope.gdd_app.ui.sample_completion.behaviours.FormTraining
import net.aiscope.gdd_app.ui.sample_completion.behaviours.FormTrainingFirstTime
import net.aiscope.gdd_app.ui.sample_completion.behaviours.FormTrainingIsComplete
import net.aiscope.gdd_app.ui.sample_completion.metadata.MetadataFragment
import net.aiscope.gdd_app.ui.sample_completion.preparation.PreparationFragment
import net.aiscope.gdd_app.ui.sample_completion.quality.QualityFragment
import net.aiscope.gdd_app.ui.showConfirmExitDialog
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbar
import net.aiscope.gdd_app.ui.snackbar.CustomSnackbarAction
import timber.log.Timber
import javax.inject.Inject

@Suppress("TooManyFunctions")
class SampleCompletionActivity : CaptureFlow, AppCompatActivity() {
    private lateinit var binding: ActivityCompleteSampleBinding

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val sharedVM: SampleCompletionViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        //Initialize the shared viewmodel for the tabs
        sharedVM.initVM()

        binding = ActivityCompleteSampleBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)
            setSupportActionBar(toolbarLayout.toolbar)
            attachCaptureFlowToolbar(toolbarLayout.toolbar)

            // Changes Form's UI/behavior based on whether user has already been trained in using
            // the form or not. Currently training is considered to be true when user has
            // successfully submitted the form at-least once
            val submitFormTrainingBehaviour =
                pickBehaviour(
                    sharedVM.hasUserSubmitSampleFirstTime()
                )

            tabLayout.tabGravity = TabLayout.GRAVITY_FILL

            viewPager.adapter = FragmentAdapter(
                this@SampleCompletionActivity
            )
            viewPager.isUserInputEnabled = submitFormTrainingBehaviour.allowTabSwitchOnScroll()

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
                    validateTabsAndUpdateVM()
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }
            })

            submitLabel(submitFormTrainingBehaviour.getSubmitLabel())

            completionSaveSample.setOnClickListener {
                submitFormTrainingBehaviour.getSubmitOnClickListener(
                    this@SampleCompletionActivity
                )
            }
        }
    }

    // The int indicates which tab has errors so we can switch to that one
    fun validateTabsAndUpdateVM(): Int? {
        val metadataFragment: MetadataFragment? =
            findFragment(0) as? MetadataFragment
        val metadataOk = metadataFragment?.validateAndUpdateVM() ?: true

        val preparationFragment: PreparationFragment? =
            findFragment(1) as? PreparationFragment
        val preparationOK = preparationFragment?.validateAndUpdateVM() ?: (
            sharedVM.isValidWaterTypeValue(sharedVM.waterType) &&
            sharedVM.isValidSampleAgeValue(sharedVM.sampleAge)
        )

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

    fun saveToVM() {
        try {
            sharedVM.save()
            finishFlow()
        } catch (@Suppress("TooGenericExceptionCaught") error: Throwable) {
            Timber.e(error, "An error occurred when saving sample completion data")
            showRetryBar()
        }
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

    @SuppressLint("MissingSuperCall")
    override fun onBackPressed() {
        showConfirmExitDialog()
    }

    fun getCurrentTab(): Int {
        with(binding) {
            return viewPager.currentItem
        }
    }

    fun setActiveTab(index: Int) {
        with(binding) {
            val tab = tabLayout.getTabAt(index)
            tab?.select()
        }
    }

    fun isCurrentTabLastStep(): Boolean {
        with(binding) {
            return (getCurrentTab() == tabLayout.tabCount-1)
        }
    }

    fun submitLabel(txt: Int)
    {
        with(binding) {
            completionSaveSample.setText(txt)
        }
    }

    fun findFragment(index: Int) =
        supportFragmentManager.findFragmentByTag("f$index")

    companion object SampleFormTrainingBehaviourFactory
    {
        fun pickBehaviour(hasSubmitFirstTime: Boolean): FormTraining
        {
            return when (hasSubmitFirstTime) {
                true -> {
                    FormTrainingIsComplete()
                }
                false -> {
                    FormTrainingFirstTime()
                }
            }
        }
    }
}
