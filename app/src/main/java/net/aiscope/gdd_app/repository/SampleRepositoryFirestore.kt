package net.aiscope.gdd_app.repository

import com.google.firebase.Timestamp
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleCollection

class SampleRepositoryFirestore @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) {
    fun store(sample: Sample) {
        val sampleCollection = SampleCollection(
            createdOn = Timestamp(sample.createdOn.time),
            uploadedBy = firebaseFirestore.collection("microscopist")
                .document(sample.microscopist),
            location = sample.id,
            numberOfImages = sample.captures.completedCaptureCount()
        )
        firebaseFirestore.collection("samples")
            .document(sampleCollection.location)
            .set(sampleCollection)
    }
}

