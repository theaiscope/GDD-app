package net.aiscope.gdd_app.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference

//Sample collection in Firestore
data class SampleCollection (
    val location: String,
    val numberOfImages: Int,
    val createdOn: Timestamp,
    val uploadedBy: DocumentReference?,
)
