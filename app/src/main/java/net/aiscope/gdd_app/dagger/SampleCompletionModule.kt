package net.aiscope.gdd_app.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionActivity

@Module
abstract class SampleCompletionModule {

    @Binds
    @PerActivity
    internal abstract fun activity(activity: SampleCompletionActivity): Activity

}
