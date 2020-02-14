package net.aiscope.gdd_app.repository

import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.extensions.toLinkedHashSet
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.MalariaStage
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.model.Status
import java.io.File

data class SampleMetadataDto(
    @SerializedName("bloodType") val bloodType: Int,
    @SerializedName("species") val species: Int,
    @SerializedName("stage") val stage: Int
)

data class SampleDto(
    val id: String,
    val healthFacility: String,
    val microscopist: String,
    val disease: String? = null,
    val imagePaths: List<String>,
    val maskPaths: List<String>,
    val metadata: SampleMetadataDto,
    val status: Short
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
            MalariaSpecies.values().first { it.id == metadata.species },
            MalariaStage.values().first { it.id == metadata.stage }
        ),
        status = Status.values().first { it.id == status }
    )
}

fun Sample.toDto() = SampleDto(
    id = id,
    healthFacility = healthFacility,
    microscopist = microscopist,
    disease = disease,
    imagePaths = images.map { it.absolutePath },
    maskPaths = masks.map { it.absolutePath },
    metadata = SampleMetadataDto(metadata.smearType.id, metadata.species.id, metadata.stage.id),
    status = status.id
)
