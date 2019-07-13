package net.aiscope.gdd_app.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.application.GddApplication
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.repository.SampleRepositorySharedPreference
import net.aiscope.gdd_app.repository.SharedPreferenceStore

@Module(subcomponents = [
    (HospitalSubComponents::class),
    (SelectDiseaseSubComponents::class),
    (MetadataSubComponents::class)
])
class AppModule {

    @Provides
    fun context(application: GddApplication): Context = application.applicationContext

    @Provides
    fun store(context: Context): SharedPreferenceStore = SharedPreferenceStore(context)

    @Provides
    fun sampleRepository(store: SharedPreferenceStore): SampleRepository = SampleRepositorySharedPreference(store)
}
