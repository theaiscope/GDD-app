package net.aiscope.gdd_app.repository

import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SmearType

data class SampleMetadataDto(
    @SerializedName("bloodType") val bloodType: Int,
    @SerializedName("species") val species: Int
) {
    fun toDomain() = SampleMetadata(
        SmearType.values().first { it.id == this.bloodType },
        MalariaSpecies.values().first { it.id == this.species }
    )
}

fun SampleMetadata.toDto() = SampleMetadataDto(smearType.id, species.id)
