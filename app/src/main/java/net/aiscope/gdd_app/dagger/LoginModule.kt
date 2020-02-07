package net.aiscope.gdd_app.dagger

import android.app.Activity
import dagger.Binds
import dagger.Module
import net.aiscope.gdd_app.ui.login.LoginActivity
import net.aiscope.gdd_app.ui.login.LoginView

@Module
abstract class LoginModule {

    @Binds
    @PerActivity
    internal abstract fun provideView(activity: LoginActivity): LoginView

    @Binds
    @PerActivity
    internal abstract fun provideActivity(activity: LoginActivity): Activity

}
