package net.aiscope.gdd_app.model

data class SampleMetadata(val bloodType: String)

data class Sample(val id: String, val disease: String, val imagePath: String?, val metadata: SampleMetadata?)