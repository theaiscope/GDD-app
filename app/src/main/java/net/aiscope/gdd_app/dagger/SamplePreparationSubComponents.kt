package net.aiscope.gdd_app.dagger

import dagger.Subcomponent
import dagger.android.AndroidInjector
import net.aiscope.gdd_app.ui.sample_preparation.SamplePreparationActivity

@PerActivity
@Subcomponent(modules = [(SamplePreparationModule::class)])
interface SamplePreparationSubComponents : AndroidInjector<SamplePreparationActivity> {
    @Subcomponent.Factory
    interface Factory : AndroidInjector.Factory<SamplePreparationActivity>
}
