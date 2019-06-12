package net.aiscope.gdd_app.dagger

import dagger.Component
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityActivity
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, PresenterModule::class])
interface AppComponent {
    fun inject(target: NewHealthFacilityActivity)
}