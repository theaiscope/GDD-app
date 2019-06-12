package net.aiscope.gdd_app.application

import android.app.Application
import net.aiscope.gdd_app.dagger.AppComponent
import net.aiscope.gdd_app.dagger.AppModule
import net.aiscope.gdd_app.dagger.DaggerAppComponent

class GddApplication : Application() {

    lateinit var gddComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        gddComponent = initDagger(this)
    }

    private fun initDagger(app: GddApplication): AppComponent =
        DaggerAppComponent.builder()
            .appModule(AppModule(app))
            .build()
}