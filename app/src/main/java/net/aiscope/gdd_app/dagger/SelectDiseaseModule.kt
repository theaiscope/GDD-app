package net.aiscope.gdd_app.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.repository.HospitalRepository
import net.aiscope.gdd_app.repository.SharedPreferencesRepository
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseaseActivity
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseasePresenter
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseaseView

@Module
abstract class SelectDiseaseModule {

    @Binds
    @PerActivity
    internal abstract fun provideView(view: SelectDiseaseActivity): SelectDiseaseView

    @Binds
    @PerActivity
    internal abstract fun activity(activity: SelectDiseaseActivity): Activity

    @Module
    companion object {
        @Provides
        @PerActivity
        @JvmStatic
        internal fun providePresenter(
            view: SelectDiseaseView,
            repository: HospitalRepository
        ): SelectDiseasePresenter =
            SelectDiseasePresenter(
                view,
                repository
            )

        @Provides
        @PerActivity
        @JvmStatic
        fun provideHospitalRepository(impl: SharedPreferencesRepository): HospitalRepository = impl
    }




}
