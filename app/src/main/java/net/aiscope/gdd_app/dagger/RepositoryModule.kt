package net.aiscope.gdd_app.dagger

import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Binds
import dagger.Module
import dagger.Provides
import net.aiscope.gdd_app.repository.FirebaseMicroscopistRepository
import net.aiscope.gdd_app.repository.FirestoreHealthFacilityRepository
import net.aiscope.gdd_app.repository.HealthFacilityRepository
import net.aiscope.gdd_app.repository.MicroscopistRepository
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.repository.SampleRepositorySharedPreference
import net.aiscope.gdd_app.repository.SharedPreferenceStore
import net.aiscope.gdd_app.repository.UUID
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    companion object {
        @Singleton
        @Provides
        fun provideSampleRepository(
            store: SharedPreferenceStore,
            healthFacilityRepository: HealthFacilityRepository,
            gson: Gson,
            firebaseFirestore: FirebaseFirestore
        ): SampleRepository =
            SampleRepositorySharedPreference(store, UUID, healthFacilityRepository, gson,
                firebaseFirestore)
    }

    @Binds
    internal abstract fun bindHealthFacilityRepository(
        firestoreHealthFacilityRepository: FirestoreHealthFacilityRepository
    ): HealthFacilityRepository

    @Binds
    internal abstract fun bindMicroscopistRepository(
        firebaseMicroscopistRepository: FirebaseMicroscopistRepository
    ): MicroscopistRepository
}
