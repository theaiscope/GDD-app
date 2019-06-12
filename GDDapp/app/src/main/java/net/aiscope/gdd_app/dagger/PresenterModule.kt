package net.aiscope.gdd_app.dagger

import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityPresenter
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityPresenterImpl
import javax.inject.Singleton

@Module
class PresenterModule {
    @Provides
    @Singleton
    fun provideNewHealthFacilityPresenter(): NewHealthFacilityPresenter = NewHealthFacilityPresenterImpl()
}