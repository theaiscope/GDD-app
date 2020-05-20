package net.aiscope.gdd_app.dagger

import android.app.Activity
import android.content.res.Resources
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.mask.MaskActivity
import net.aiscope.gdd_app.ui.mask.MaskPresenter
import net.aiscope.gdd_app.ui.mask.MaskView

@Module
abstract class MaskModule {

    @Binds
    @PerActivity
    internal abstract fun provideView(view: MaskActivity): MaskView

    @Binds
    @PerActivity
    internal abstract fun activity(activity: MaskActivity): Activity

    companion object {
        @Provides
        @PerActivity
        internal fun providePresenter(
            view: MaskView,
            repository: SampleRepository,
            resources: Resources
        ): MaskPresenter =
            MaskPresenter(view, repository, resources)
    }
}
