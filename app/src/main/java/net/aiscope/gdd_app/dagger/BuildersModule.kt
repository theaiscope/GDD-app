package net.aiscope.gdd_app.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import dagger.android.ActivityKey
import dagger.android.AndroidInjector
import dagger.multibindings.IntoMap
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.mask.MaskActivity
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
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

    @Binds
    @IntoMap
    @ActivityKey(MetadataActivity::class)
    abstract fun bindMetadataActivityInjectorFactory(builder: MetadataSubComponents.Builder):
            AndroidInjector.Factory<out Activity>

    @Binds
    @IntoMap
    @ActivityKey(CaptureImageActivity::class)
    abstract fun bindCaptureImageActivityInjectorFactory(builder: CaptureImageSubComponents.Builder):
            AndroidInjector.Factory<out Activity>

    @Binds
    @IntoMap
    @ActivityKey(MaskActivity::class)
    abstract fun bindMaskActivityInjectorFactory(builder: MaskSubComponents.Builder):
            AndroidInjector.Factory<out Activity>
}