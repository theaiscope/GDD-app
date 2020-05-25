package net.aiscope.gdd_app.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.ui.sample_preparation.SamplePreparationActivity
import net.aiscope.gdd_app.ui.sample_preparation.SamplePreparationMapper
import net.aiscope.gdd_app.ui.sample_preparation.SamplePreparationView

@Module
abstract class SamplePreparationModule {

    @Binds
    @PerActivity
    internal abstract fun provideView(view: SamplePreparationActivity): SamplePreparationView

    @Binds
    @PerActivity
    internal abstract fun activity(activity: SamplePreparationActivity): Activity

    companion object {
        @Provides
        @PerActivity
        internal fun provideMapper(): SamplePreparationMapper = SamplePreparationMapper
    }
}
