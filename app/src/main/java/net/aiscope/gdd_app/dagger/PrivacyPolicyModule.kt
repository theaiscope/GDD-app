package net.aiscope.gdd_app.dagger

import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.ui.policy.PrivacyPolicyActivity
import net.aiscope.gdd_app.ui.policy.PrivacyPolicyPresenter
import net.aiscope.gdd_app.ui.policy.PrivacyPolicyView

@Module
abstract class PrivacyPolicyModule {

    @Binds
    @PerActivity
    internal abstract fun provideView(view: PrivacyPolicyActivity): PrivacyPolicyView

}
