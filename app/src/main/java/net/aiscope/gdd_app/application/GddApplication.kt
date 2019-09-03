package net.aiscope.gdd_app.application

import android.app.Application
import android.view.TextureView
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.crashlytics.android.Crashlytics
import com.smartlook.sdk.smartlook.Smartlook
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import net.aiscope.gdd_app.BuildConfig
import net.aiscope.gdd_app.dagger.DaggerAppComponent
import org.jetbrains.annotations.NotNull
import timber.log.Timber
import javax.inject.Inject

class GddApplication : Application(), HasAndroidInjector {

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any>

    @Inject
    lateinit var workerFactory: WorkerFactory

    override fun androidInjector() = androidInjector

    override fun onCreate() {
        super.onCreate()
        // initialize Dagger
        DaggerAppComponent.builder().application(this).build().inject(this)

        val conf = Configuration.Builder().setWorkerFactory(workerFactory).build()
        WorkManager.initialize(this, conf)

        if (!BuildConfig.DEBUG) {
            Smartlook.setupAndStartRecording(BuildConfig.SMARTLOOK_API_KEY)
            Smartlook.registerBlacklistedClass(TextureView::class.java)
            Smartlook.enableCrashlytics(true)
            Timber.plant(CrashlyticsReportingTree())
        } else {
            Timber.plant(Timber.DebugTree())
        }
    }
}

/** A tree which logs information for crashlytics reporting. */
class CrashlyticsReportingTree : @NotNull Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Crashlytics.log(priority, tag, message)
        t?.also { Crashlytics.logException(it) }
    }
}
