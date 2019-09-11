package net.aiscope.gdd_app.model

import net.aiscope.gdd_app.extensions.plus
import java.io.File

enum class Status(val id: Short) {
    Incomplete(0), ReadyToUpload(1), Uploaded(2)
}

enum class SmearType(val id: Int) {
    THIN(1),
    THICK(2)
}

data class SampleMetadata(val smearType: SmearType = SmearType.THIN,
                          val specie: String = "",
                          val stage: String = "")

data class Sample(
    val id: String,
    val healthFacility: String,
    val disease: String? = null,
    val images: LinkedHashSet<File> = linkedSetOf(),
    val masks: LinkedHashSet<File> = linkedSetOf(),
    val metadata: SampleMetadata = SampleMetadata(),
    val status: Status = Status.Incomplete
) {
    fun addImage(path: File) = copy(images = images + path)

    fun addMask(path: File) = copy(masks = masks + path)

    fun nextImageName(): String = "${id}_image_${images.size}"

    fun nextMaskName(): String = "${id}_mask_${images.size}"
}
