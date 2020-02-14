package net.aiscope.gdd_app.dagger

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.login.LoginActivity
import net.aiscope.gdd_app.ui.mask.MaskActivity
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseaseActivity

@Module
abstract class BuildersModule {

    @PerActivity
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun contributesAndroidInjector(): LoginActivity

    @Binds
    @IntoMap
    @ClassKey(SelectDiseaseActivity::class)
    abstract fun bindSelectDiseaseActivityInjectorFactory(factory: SelectDiseaseSubComponents.Factory):
            AndroidInjector.Factory<*>

    @Binds
    @IntoMap
    @ClassKey(MetadataActivity::class)
    abstract fun bindMetadataActivityInjectorFactory(factory: MetadataSubComponents.Factory):
            AndroidInjector.Factory<*>

    @Binds
    @IntoMap
    @ClassKey(CaptureImageActivity::class)
    abstract fun bindCaptureImageActivityInjectorFactory(factory: CaptureImageSubComponents.Factory):
            AndroidInjector.Factory<*>

    @Binds
    @IntoMap
    @ClassKey(MaskActivity::class)
    abstract fun bindMaskActivityInjectorFactory(factory: MaskSubComponents.Factory):
            AndroidInjector.Factory<*>
}
