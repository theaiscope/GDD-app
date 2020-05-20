package net.aiscope.gdd_app.model

import net.aiscope.gdd_app.extensions.plus
import java.io.File
import java.util.Calendar
import java.util.LinkedHashSet

data class Sample(
    val id: String,
    val healthFacility: String,
    val microscopist: String,
    val disease: String? = null,
    val preparation: SamplePreparation? = null,
    val microscopeQuality: MicroscopeQuality? = null,
    val images: LinkedHashSet<File> = linkedSetOf(),
    val masks: LinkedHashSet<File> = linkedSetOf(),
    val metadata: SampleMetadata = SampleMetadata(),
    val status: SampleStatus = SampleStatus.Incomplete,
    val createdOn: Calendar? = null
) {
    fun addImage(path: File) = copy(images = images + path)

    fun addMask(path: File) = copy(masks = masks + path)

    fun nextImageName(): String = "${id}_image_${images.size}"

    fun nextMaskName(): String = "${id}_mask_${images.size}"
}
