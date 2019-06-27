package net.aiscope.gdd_app.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityActivity
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseaseActivity

@Module
abstract class BuildersModule {
    @Binds
    @IntoMap
    @ActivityKey(NewHealthFacilityActivity::class)
    abstract fun bindNewHealthFacilityActivityInjectorFactory(builder: HospitalSubComponents.Builder):
            AndroidInjector.Factory<out Activity>

    @Binds
    @IntoMap
    @ActivityKey(SelectDiseaseActivity::class)
    abstract fun bindSelectDiseaseActivityInjectorFactory(builder: SelectDiseaseSubComponents.Builder):
            AndroidInjector.Factory<out Activity>

}