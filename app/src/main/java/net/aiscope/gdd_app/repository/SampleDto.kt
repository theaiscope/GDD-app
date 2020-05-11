package net.aiscope.gdd_app.repository

import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.extensions.toLinkedHashSet
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.model.Status
import java.io.File
import java.util.Calendar

data class SampleMetadataDto(
    @SerializedName("bloodType") val bloodType: Int,
    @SerializedName("species") val species: Int
)

data class SampleDto(
    val id: String,
    val healthFacility: String,
    val microscopist: String,
    val disease: String? = null,
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
        images = imagePaths.map { File(it) }.toLinkedHashSet(),
        masks = maskPaths.map { File(it) }.toLinkedHashSet(),
        metadata = SampleMetadata(
            SmearType.values().first { it.id == metadata.bloodType },
            MalariaSpecies.values().first { it.id == metadata.species }
        ),
        status = Status.values().first { it.id == status },
        createdOn = createdOn
    )
}

fun Sample.toDto() = SampleDto(
    id = id,
    healthFacility = healthFacility,
    microscopist = microscopist,
    disease = disease,
    imagePaths = images.map { it.absolutePath },
    maskPaths = masks.map { it.absolutePath },
    metadata = SampleMetadataDto(metadata.smearType.id, metadata.species.id),
    status = status.id,
    createdOn = createdOn
)
