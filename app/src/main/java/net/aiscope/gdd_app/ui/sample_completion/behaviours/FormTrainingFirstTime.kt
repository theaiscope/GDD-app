package net.aiscope.gdd_app.ui.sample_completion.behaviours

import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionActivity
import net.aiscope.gdd_app.ui.sample_completion.SampleFormFragment

class FormTrainingFirstTime  : FormTraining {
    override fun getSubmitOnClickListener(sa: SampleCompletionActivity) {
        val fmt: SampleFormFragment? =
            sa.findFragment(sa.getCurrentTab()) as? SampleFormFragment
        val fmtOk = fmt?.validateAndUpdateVM() ?: true
        if(fmtOk)
        {
            if(sa.isCurrentTabLastStep())
            {
                sa.save()
            }
            else
            {
                sa.setActiveTab(sa.getCurrentTab()+1)
            }
        }
    }
    override fun getSubmitLabel(): Int {
        return R.string.complete_sample_first_time_action_label
    }
    override fun allowScroll(): Boolean
    {
        return false
    }
}