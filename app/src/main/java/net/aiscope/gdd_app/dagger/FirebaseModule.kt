package net.aiscope.gdd_app.dagger

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides

@Module
object FirebaseModule {

    @Provides
    fun providesFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun firestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

}
