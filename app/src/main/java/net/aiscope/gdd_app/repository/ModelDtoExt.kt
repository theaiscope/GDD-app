package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SamplePreparation

fun Sample.toDto() = SampleDto(
    id = id,
    healthFacility = healthFacility,
    microscopist = microscopist,
    disease = disease,
    preparation = preparation?.toDto(),
    microscopeQuality = microscopeQuality?.toDto(),
    imagePaths = images.map { it.absolutePath },
    maskPaths = masks.map { it.absolutePath },
    metadata = SampleMetadataDto(metadata.smearType.id, metadata.species.id),
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
