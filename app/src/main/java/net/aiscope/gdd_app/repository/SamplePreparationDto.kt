package net.aiscope.gdd_app.repository

import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.model.WaterType

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

fun SamplePreparation.toDto() = SamplePreparationDto(
    waterType.id,
    usesGiemsa,
    giemsaFP,
    usesPbs,
    usesAlcohol,
    reusesSlides
)
