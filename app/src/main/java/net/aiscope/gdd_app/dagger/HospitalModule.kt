package net.aiscope.gdd_app.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.repository.HospitalRepository
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityActivity
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityPresenter
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityView

@Module
abstract class HospitalModule {

    @Binds
    @PerActivity
    internal abstract fun provideView(view: NewHealthFacilityActivity): NewHealthFacilityView

    @Binds
    @PerActivity
    internal abstract fun activity(activity: NewHealthFacilityActivity): Activity

    @Module
    companion object {
        @Provides
        @PerActivity
        @JvmStatic
        internal fun providePresenter(
            view: NewHealthFacilityView,
            repository: HospitalRepository
        ): NewHealthFacilityPresenter =
            NewHealthFacilityPresenter(
                view,
                repository
            )
    }
}
