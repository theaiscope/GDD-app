package net.aiscope.gdd_app.dagger

import dagger.Subcomponent
import dagger.android.AndroidInjector
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity

@PerActivity
@Subcomponent(modules = [(CaptureImageModule::class)])
interface CaptureImageSubComponents : AndroidInjector<CaptureImageActivity> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<CaptureImageActivity>
}

