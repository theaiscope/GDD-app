package net.aiscope.gdd_app.dagger

import dagger.Binds
import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import net.aiscope.gdd_app.coroutines.DefaultDispatcherProvider
import net.aiscope.gdd_app.coroutines.DispatcherProvider
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.login.LoginActivity
import net.aiscope.gdd_app.ui.main.MainActivity
import net.aiscope.gdd_app.ui.mask.MaskActivity
import net.aiscope.gdd_app.ui.policy.PrivacyPolicyActivity
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionActivity

@Module
abstract class BuildersModule {

    @PerActivity
    @ContributesAndroidInjector(modules = [LoginModule::class])
    abstract fun contributesLoginActivityAndroidInjector(): LoginActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [PrivacyPolicyModule::class])
    abstract fun contributesPrivacyPolicyActivityAndroidInjector(): PrivacyPolicyActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [MainModule::class])
    abstract fun contributesMainActivityAndroidInjector(): MainActivity

    @PerActivity
    @ContributesAndroidInjector(modules = [SampleCompletionModule::class])
    abstract fun contributesSampleCompletionActivityAndroidInjector(): SampleCompletionActivity

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

    @Binds
    internal abstract fun bindDispatcherProvider(dispatcherProvider: DefaultDispatcherProvider): DispatcherProvider
}
