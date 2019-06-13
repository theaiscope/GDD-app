package net.aiscope.gdd_app.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityActivity

@Module
abstract class BuildersModule {
    @Binds
    @IntoMap
    @ActivityKey(NewHealthFacilityActivity::class)
    abstract fun bindNewHealthFacilityActivityInjectorFactory(builder: HospitalSubComponents.Builder):
            AndroidInjector.Factory<out Activity>

}