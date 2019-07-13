package net.aiscope.gdd_app.dagger

import dagger.Subcomponent
import dagger.android.AndroidInjector
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity

@PerActivity
@Subcomponent(modules = [(CaptureImageModule::class)])
interface CaptureImageSubComponents : AndroidInjector<CaptureImageActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<CaptureImageActivity>()
}

