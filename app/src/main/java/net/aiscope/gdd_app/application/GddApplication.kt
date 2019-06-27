package net.aiscope.gdd_app.application

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import net.aiscope.gdd_app.dagger.DaggerAppComponent
import javax.inject.Inject

class GddApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun onCreate() {
        super.onCreate()
        // initialize Dagger
        DaggerAppComponent.builder().application(this).build().inject(this)


    }
}