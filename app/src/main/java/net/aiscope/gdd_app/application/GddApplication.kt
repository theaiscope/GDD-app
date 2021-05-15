package net.aiscope.gdd_app.application

import android.app.Application
import android.util.Log
import android.view.TextureView
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.smartlook.sdk.smartlook.Smartlook
import com.smartlook.sdk.smartlook.integrations.model.FirebaseCrashlyticsIntegration
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import net.aiscope.gdd_app.BuildConfig
import net.aiscope.gdd_app.dagger.DaggerAppComponent
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
            Smartlook.enableIntegration(FirebaseCrashlyticsIntegration())
            Timber.plant(CrashlyticsReportingTree())
        } else {
            Timber.plant(Timber.DebugTree())
        }
    }
}

/** A tree which logs information for crashlytics reporting. */
class CrashlyticsReportingTree : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        val level = when (priority) {
            Log.VERBOSE -> "V"
            Log.DEBUG -> "D"
            Log.INFO -> "I"
            Log.WARN -> "W"
            Log.ERROR -> "E"
            Log.ASSERT -> "A"
            else -> "?"
        }
        FirebaseCrashlytics.getInstance().log("$level${tag?.let {"/$it"}}: $message")
        t?.also { FirebaseCrashlytics.getInstance().recordException(it) }
    }
}
