package net.aiscope.gdd_app.ui.sample_completion.behaviours

import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionActivity

class FormTrainingIsComplete : FormTraining {
    override fun getSubmitOnClickListener(sa: SampleCompletionActivity) {
        val erroneousTab = sa.validateTabsAndUpdateVM()
        if (erroneousTab == null) {
            sa.save()
        } else {
            sa.setActiveTab(erroneousTab)
        }
    }

    override fun getSubmitLabel() : Int
    {
        return R.string.complete_sample_save_sample

    }
    override  fun allowScroll(): Boolean
    {
        return true
    }
}
