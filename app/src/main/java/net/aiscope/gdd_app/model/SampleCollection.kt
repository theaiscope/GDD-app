package net.aiscope.gdd_app.model

//Sample collection in Firestore
class SampleCollection (
    val sampleID: String,
    val location: String,
    val numberOfImages: Int,
    val createdOn: String,
    val uploadedBy: String,
){}