package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.extensions.toLinkedHashSet
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.model.SampleStatus
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.model.WaterType
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

data class SamplePreparationDto(
    val waterType: Int,
    val usesGiemsa: Boolean,
    val giemsaFP: Boolean,
    val usesPbs: Boolean,
    val usesAlcohol: Boolean,
    val reusesSlides: Boolean
) {
    fun toDomain() = SamplePreparation(
        WaterType.values().first { it.id == waterType },
        usesGiemsa,
        giemsaFP,
        usesPbs,
        usesAlcohol,
        reusesSlides
    )
}

data class MicroscopeQualityDto(
    val isDamaged: Boolean,
    val magnification: Int
) {
    fun toDomain() = MicroscopeQuality(
        isDamaged,
        magnification
    )
}

data class SampleMetadataDto(
    val bloodType: Int,
    val species: Int
) {
    fun toDomain() = SampleMetadata(
        SmearType.values().first { it.id == this.bloodType },
        MalariaSpecies.values().first { it.id == this.species }
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

fun SamplePreparation.toDto() = SamplePreparationDto(
    waterType.id,
    usesGiemsa,
    giemsaFP,
    usesPbs,
    usesAlcohol,
    reusesSlides
)

fun MicroscopeQuality.toDto() = MicroscopeQualityDto(
    isDamaged,
    magnification
)

fun SampleMetadata.toDto() = SampleMetadataDto(smearType.id, species.id)
