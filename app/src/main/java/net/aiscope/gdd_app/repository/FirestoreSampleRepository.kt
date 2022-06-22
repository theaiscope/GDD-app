package net.aiscope.gdd_app.repository

import com.google.firebase.firestore.FirebaseFirestore
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleCollection
import net.aiscope.gdd_app.network.ISO_FORMAT
import javax.inject.Inject

class FirestoreSampleRepository @Inject constructor(
    private val firestore: FirebaseFirestore
): SampleCollectionRepository {
    override fun store(sampleCollection: SampleCollection) {
        firestore.collection("samples").document(sampleCollection.sampleID).set(sampleCollection)
    }

    override fun store(sample: Sample) {

        val sampleCollection = SampleCollection(
            sampleID = sample.id,
            createdOn = ISO_FORMAT.format(sample.createdOn.time),
            uploadedBy = sample.microscopist,
            location = "${sample.id}/metadata.json",
            numberOfImages = sample.captures.completedCaptureCount()
        )
        store(sampleCollection)
    }
}