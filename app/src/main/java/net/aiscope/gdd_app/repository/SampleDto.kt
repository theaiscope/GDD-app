package net.aiscope.gdd_app.repository

import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.extensions.toLinkedHashSet
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.model.Status
import java.io.File

data class SampleMetadataDto(
    @SerializedName("bloodType") val bloodType: Int
)

data class SampleDto(
    val id: String,
    val healthFacility: String,
    val disease: String? = null,
    val imagePaths: List<String>,
    val maskPaths: List<String>,
    val metadata: SampleMetadataDto,
    val status: Short
) {
    fun toDomain(): Sample = Sample(
        id = id,
        healthFacility = healthFacility,
        disease = disease,
        images = imagePaths.map { File(it) }.toLinkedHashSet(),
        masks = maskPaths.map { File(it) }.toLinkedHashSet(),
        metadata = SampleMetadata(SmearType.values().first { it.id == metadata.bloodType }),
        status = Status.values().first { it.id == status }
    )
}

fun Sample.toDto() = SampleDto(
    id = id,
    healthFacility = healthFacility,
    disease = disease,
    imagePaths = images.map { it.absolutePath },
    maskPaths = masks.map { it.absolutePath },
    metadata = SampleMetadataDto(metadata.smearType.id),
    status = status.id
)
