package net.aiscope.gdd_app.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import net.aiscope.gdd_app.coroutines.DispatcherProvider
import net.aiscope.gdd_app.model.HealthFacility
import timber.log.Timber
import javax.inject.Inject

class FirestoreHealthFacilityRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val dispatchers: DispatcherProvider
) : HealthFacilityRepository {

    override suspend fun load(): HealthFacility = withContext(dispatchers.io()) {
        val user = firebaseAuth.currentUser ?: error("No current user found")
        val snapshot: QuerySnapshot = healthFacilityQueryFor(user).get().await()
        val document: DocumentSnapshot = snapshot.documents[0]
        HealthFacility(document["name"] as String, document.id, user.uid)
    }

    override fun cacheHealthFacility() {
        val user = firebaseAuth.currentUser ?: error("No current user found")
        var listener: ListenerRegistration? = null
        listener = healthFacilityQueryFor(user)
            .addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, ex ->
                Timber.tag("cacheHealthFacility")
                    .i(ex, "got snapshot %s, cache: %s", snapshot, snapshot?.metadata?.isFromCache)
                if (snapshot?.metadata?.isFromCache == false) {
                    listener?.remove()
                }
            }
    }

    private fun healthFacilityQueryFor(user: FirebaseUser) =
        firestore.collection("facilities")
            .whereArrayContains("microscopists", getMicroscopistReference(user))

    private fun getMicroscopistReference(user: FirebaseUser): DocumentReference {
        return firestore.collection("microscopists").document(user.uid)
    }
}
