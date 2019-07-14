package net.aiscope.gdd_app.dagger

import android.content.Context
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.repository.*
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    fun store(context: Context): SharedPreferenceStore = SharedPreferenceStore(context)

    @Singleton
    @Provides
    fun sampleRepository(store: SharedPreferenceStore, hospitalRepository: HospitalRepository): SampleRepository =
        SampleRepositorySharedPreference(store, UUID, hospitalRepository)

    @Provides
    fun provideHospitalRepository(context: Context): HospitalRepository = SharedPreferencesRepository(context)
}