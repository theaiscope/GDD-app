package net.aiscope.gdd_app.application

import android.app.Activity
import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import net.aiscope.gdd_app.dagger.DaggerAppComponent
import javax.inject.Inject

class GddApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var workerFactory: WorkerFactory

    override fun activityInjector(): AndroidInjector<Activity> = activityInjector

    override fun onCreate() {
        super.onCreate()
        // initialize Dagger
        DaggerAppComponent.builder().application(this).build().inject(this)

        val conf = Configuration.Builder().setWorkerFactory(workerFactory).build()
        WorkManager.initialize(this, conf)
    }
}