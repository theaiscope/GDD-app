package net.aiscope.gdd_app.dagger

import dagger.Subcomponent
import dagger.android.AndroidInjector
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.mask.MaskActivity

@PerActivity
@Subcomponent(modules = [(MaskModule::class)])
interface MaskSubComponents : AndroidInjector<MaskActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<MaskActivity>()
}

