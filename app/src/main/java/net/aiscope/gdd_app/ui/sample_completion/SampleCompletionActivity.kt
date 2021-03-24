package net.aiscope.gdd_app.ui.sample_completion

import android.os.Bundle
import android.view.View
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
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.goToHomeAndConfirmSaved
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

        //FIXME Bit sloppy
        val context = this

        binding = ActivityCompleteSampleBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)
            setSupportActionBar(toolbarLayout.toolbar)

            tabLayout.addTab(tabLayout.newTab().setText(R.string.complete_sample_metadata_tab))
            tabLayout.addTab(tabLayout.newTab().setText(R.string.complete_sample_sampleprep_tab))
            tabLayout.addTab(tabLayout.newTab().setText(R.string.complete_sample_quality_tab))
            tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            val adapter = FragmentAdapter(
                context, supportFragmentManager,
                tabLayout.tabCount
            )
            viewPager.adapter = adapter
            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                //TODO: anything that needs doing in these cases??
                override fun onTabUnselected(tab: TabLayout.Tab) {
                    //call the validate and write back to VM function
                }
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })

            completionSaveSample.setOnClickListener { save() }

        }

        //Initialize the shared viewmodel for the tabs
        lifecycleScope.launch {
            sharedVM.initVM()
        }
    }

    fun save() {
        //So how do I get the fragments??
//        supportFragmentManager.findFragmentById()
        lifecycleScope.launch {
            try{
                sharedVM.save()
                finishFlow()
            } catch(@Suppress("TooGenericExceptionCaught") error: Throwable) {
                Timber.e(error, "An error occurred when saving sample preparation")
                showRetryBar()
            }
        }
    }

    fun showRetryBar() {
        CustomSnackbar.make(
            findViewById(android.R.id.content),
            getString(R.string.microscope_quality_snackbar_error),
            Snackbar.LENGTH_INDEFINITE, null,
            CustomSnackbarAction(
                getString(R.string.microscope_quality_snackbar_retry),
                View.OnClickListener {
                    lifecycleScope.launch {
                        sharedVM.save()
                    }
                })
        ).show()
    }

    private fun finishFlow() {
        goToHomeAndConfirmSaved()
    }
}
