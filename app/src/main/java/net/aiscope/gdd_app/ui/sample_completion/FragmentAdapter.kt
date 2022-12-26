package net.aiscope.gdd_app.ui.sample_completion

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import net.aiscope.gdd_app.ui.sample_completion.metadata.MetadataFragment
import net.aiscope.gdd_app.ui.sample_completion.preparation.PreparationFragment
import net.aiscope.gdd_app.ui.sample_completion.quality.QualityFragment

internal class FragmentAdapter(
    fa: FragmentActivity
) : FragmentStateAdapter(fa) {
    override fun createFragment(position: Int): Fragment {
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
    override fun getItemCount(): Int = 3
}
