package net.aiscope.gdd_app.model

enum class SampleStatus(val id: Short) {
    Incomplete(0),
    ReadyToUpload(1),
    Uploaded(2)
}
