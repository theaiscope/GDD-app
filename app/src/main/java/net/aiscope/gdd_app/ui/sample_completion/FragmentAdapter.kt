package net.aiscope.gdd_app.ui.sample_completion

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import net.aiscope.gdd_app.ui.sample_completion.metadata.MetadataFragment
import net.aiscope.gdd_app.ui.sample_completion.preparation.PreparationFragment
import net.aiscope.gdd_app.ui.sample_completion.quality.QualityFragment

internal class FragmentAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                MetadataFragment()
            }
            1 -> {
                PreparationFragment()
            }
            2 -> {
                QualityFragment()
            }
            else -> MetadataFragment()
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}
