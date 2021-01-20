package net.aiscope.gdd_app.ui.sample_completion

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import dagger.android.AndroidInjection
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ActivityCompleteSampleBinding
import net.aiscope.gdd_app.databinding.ActivityMetadataBinding
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar

class SampleCompletionActivity:  AppCompatActivity() {
    private lateinit var binding: ActivityCompleteSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        //AndroidInjection.inject(this)
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
            val adapter = FragmentAdapter(context, supportFragmentManager,
                tabLayout.tabCount)
            viewPager.adapter = adapter
            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }
                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
        }
    }
}