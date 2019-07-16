package net.aiscope.gdd_app.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.application.GddApplication
import net.aiscope.gdd_app.model.HealthFacility
import net.aiscope.gdd_app.repository.*
import javax.inject.Singleton

@Module(subcomponents = [
    (HospitalSubComponents::class),
    (SelectDiseaseSubComponents::class),
    (MetadataSubComponents::class),
    (CaptureImageSubComponents::class),
    (MaskSubComponents::class)
])
class AppModule {

    @Provides
    fun context(application: GddApplication): Context = application.applicationContext

}
