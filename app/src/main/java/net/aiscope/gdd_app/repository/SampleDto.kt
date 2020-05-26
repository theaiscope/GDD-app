package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.extensions.toLinkedHashSet
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleStatus
import java.io.File
import java.util.Calendar

data class SampleDto(
    val id: String,
    val healthFacility: String,
    val microscopist: String,
    val disease: String? = null,
    val preparation: SamplePreparationDto?,
    val microscopeQuality: MicroscopeQualityDto?,
    val imagePaths: List<String>,
    val maskPaths: List<String>,
    val metadata: SampleMetadataDto,
    val status: Short,
    val createdOn: Calendar? = null
) {
    fun toDomain(): Sample = Sample(
        id = id,
        healthFacility = healthFacility,
        microscopist = microscopist,
        disease = disease,
        preparation = preparation?.toDomain(),
        microscopeQuality = microscopeQuality?.toDomain(),
        images = imagePaths.map { File(it) }.toLinkedHashSet(),
        masks = maskPaths.map { File(it) }.toLinkedHashSet(),
        metadata = metadata.toDomain(),
        status = SampleStatus.values().first { it.id == status },
        createdOn = createdOn
    )
}

fun Sample.toDto() = SampleDto(
    id = id,
    healthFacility = healthFacility,
    microscopist = microscopist,
    disease = disease,
    preparation = preparation?.toDto(),
    microscopeQuality = microscopeQuality?.toDto(),
    imagePaths = images.map { it.absolutePath },
    maskPaths = masks.map { it.absolutePath },
    metadata = metadata.toDto(),
    status = status.id,
    createdOn = createdOn
)