package net.aiscope.gdd_app.repository

import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.model.WaterType

data class SamplePreparationDto(
    @SerializedName("waterType") val waterType: Int,
    @SerializedName("usesGiemsa") val usesGiemsa: Boolean,
    @SerializedName("giemsaFP") val giemsaFP: Boolean,
    @SerializedName("usesPbs") val usesPbs: Boolean,
    @SerializedName("usesAlcohol") val usesAlcohol: Boolean,
    @SerializedName("reusesSlides") val reusesSlides: Boolean
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