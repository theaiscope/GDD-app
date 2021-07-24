package net.aiscope.gdd_app.ui.sample_completion.behaviours

import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionActivity

interface FormTraining {
    fun getSubmitOnClickListener(sa: SampleCompletionActivity)
    fun getSubmitLabel(): Int
    fun allowScroll(): Boolean
}