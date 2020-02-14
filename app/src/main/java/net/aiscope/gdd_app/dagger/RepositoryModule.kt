package net.aiscope.gdd_app.dagger

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.coroutines.DispatcherProvider
import net.aiscope.gdd_app.repository.FirestoreHealthFacilityRepository
import net.aiscope.gdd_app.repository.HealthFacilityRepository
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.repository.SampleRepositorySharedPreference
import net.aiscope.gdd_app.repository.SharedPreferenceStore
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
        healthFacilityRepository: HealthFacilityRepository,
        gson: Gson
    ): SampleRepository =
        SampleRepositorySharedPreference(store, UUID, healthFacilityRepository, gson)

    @Provides
    fun provideHealthFacilityRepository(
        firestore: FirebaseFirestore,
        firebaseAuth: FirebaseAuth,
        dispatchers: DispatcherProvider
    ): HealthFacilityRepository =
        FirestoreHealthFacilityRepository(firestore, firebaseAuth, dispatchers)
}
