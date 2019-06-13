package net.aiscope.gdd_app.dagger

import dagger.Subcomponent
import dagger.android.AndroidInjector
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityActivity

@PerActivity
@Subcomponent(modules = [(HospitalModule::class)])
interface HospitalSubComponents : AndroidInjector<NewHealthFacilityActivity> {
    @Subcomponent.Builder
    abstract class Builder : AndroidInjector.Builder<NewHealthFacilityActivity>()
}