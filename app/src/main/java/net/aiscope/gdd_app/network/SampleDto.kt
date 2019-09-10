package net.aiscope.gdd_app.network

import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.model.Sample

data class SampleMetadataDto(
    @SerializedName("bloodType") val bloodType: Int
)

data class SampleDto(
    @SerializedName("id") val id: String,
    @SerializedName("healthFacility") val healthFacility: String,
    @SerializedName("disease") val disease: String,
    @SerializedName("metadata") val metadata: SampleMetadataDto
)

fun Sample.toDto() = SampleDto(
    id = id,
    healthFacility = healthFacility,
    disease = disease ?: "",
    metadata = SampleMetadataDto(bloodType = metadata.smearType.id)

)
