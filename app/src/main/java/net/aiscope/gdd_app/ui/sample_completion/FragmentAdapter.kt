package net.aiscope.gdd_app.ui.sample_completion

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

internal class FragmentAdapter(
    var context: Context,
    fm: FragmentManager,
    var totalTabs: Int
) :
    FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        //TODO: 3 different fragments
        return when (position) {
            0 -> {
                MetadataFragment()
            }
            1 -> {
                MetadataFragment()
            }
            2 -> {
                MetadataFragment()
            }
            else -> MetadataFragment()
        }
    }

    override fun getCount(): Int {
        return totalTabs
    }
}