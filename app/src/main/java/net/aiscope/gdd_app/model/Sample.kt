package net.aiscope.gdd_app.model

data class SampleMetadata(val bloodType: Long)
data class Sample(val id: String, val healthFacility: String, val disease: String? = null,
                  val imagePath: String? = null, val metadata: SampleMetadata? = null,
                  val readyToUpload: Boolean = false)