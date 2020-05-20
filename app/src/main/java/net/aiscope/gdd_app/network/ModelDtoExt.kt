package net.aiscope.gdd_app.network

import android.os.Build
import net.aiscope.gdd_app.BuildConfig
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.repository.MicroscopeQualityDto
import net.aiscope.gdd_app.repository.SampleMetadataDto
import net.aiscope.gdd_app.repository.SamplePreparationDto

fun Sample.toDto() = SampleDto(
    id = id,
    healthFacility = healthFacility,
    microscopist = microscopist,
    disease = disease ?: "",
    preparation = preparation?.toDto(),
    microscopeQuality = microscopeQuality?.toDto(),
    metadata = SampleMetadataDto(
        bloodType = metadata.smearType.id,
        species = metadata.species.id
    ),
    appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
    device = "${Build.MANUFACTURER} ${Build.MODEL}"
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
