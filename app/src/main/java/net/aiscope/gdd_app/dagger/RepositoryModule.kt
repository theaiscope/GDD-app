package net.aiscope.gdd_app.dagger

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.repository.HospitalRepository
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.repository.SampleRepositorySharedPreference
import net.aiscope.gdd_app.repository.SharedPreferenceStore
import net.aiscope.gdd_app.repository.SharedPreferencesRepository
import net.aiscope.gdd_app.repository.UUID
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Provides
    fun store(context: Context): SharedPreferenceStore = SharedPreferenceStore(context)

    @Singleton
    @Provides
    fun sampleRepository(
        store: SharedPreferenceStore,
        hospitalRepository: HospitalRepository,
        gson: Gson
    ): SampleRepository =
        SampleRepositorySharedPreference(store, UUID, hospitalRepository, gson)

    @Provides
    fun provideHospitalRepository(context: Context): HospitalRepository =
        SharedPreferencesRepository(context)
}
