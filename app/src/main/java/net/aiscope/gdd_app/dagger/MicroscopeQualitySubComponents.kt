package net.aiscope.gdd_app.dagger

import dagger.Subcomponent
import dagger.android.AndroidInjector
import net.aiscope.gdd_app.ui.microscope_quality.MicroscopeQualityActivity

@PerActivity
@Subcomponent(modules = [(MicroscopeQualityModule::class)])
interface MicroscopeQualitySubComponents : AndroidInjector<MicroscopeQualityActivity> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<MicroscopeQualityActivity>
}
