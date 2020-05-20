package net.aiscope.gdd_app.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.capture.CaptureImagePresenter
import net.aiscope.gdd_app.ui.capture.CaptureImageView

@Module
abstract class CaptureImageModule {

    @Binds
    @PerActivity
    internal abstract fun provideView(view: CaptureImageActivity): CaptureImageView

    @Binds
    @PerActivity
    internal abstract fun activity(activity: CaptureImageActivity): Activity

    companion object {
        @Provides
        @PerActivity
        internal fun providePresenter(view: CaptureImageView, repository: SampleRepository): CaptureImagePresenter =
            CaptureImagePresenter(view, repository)
    }
}
