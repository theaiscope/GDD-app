package net.aiscope.gdd_app.repository

import com.google.gson.annotations.SerializedName

data class SampleMetadataDto(
    @SerializedName("bloodType") val bloodType: Int,
    @SerializedName("species") val species: Int
)
