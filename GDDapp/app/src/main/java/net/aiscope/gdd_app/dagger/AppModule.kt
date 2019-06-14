package net.aiscope.gdd_app.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.application.GddApplication

@Module(subcomponents = [
    (HospitalSubComponents::class),
    (SelectDiseaseSubComponents::class)
])
class AppModule {

    @Provides
    fun context(application: GddApplication): Context = application.applicationContext
}