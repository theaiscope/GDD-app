package net.aiscope.gdd_app.network

import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.repository.MicroscopeQualityDto
import net.aiscope.gdd_app.repository.SampleMetadataDto
import net.aiscope.gdd_app.repository.SamplePreparationDto

data class SampleDto(
    @SerializedName("id") val id: String,
    @SerializedName("healthFacility") val healthFacility: String,
    @SerializedName("microscopist") val microscopist: String,
    @SerializedName("disease") val disease: String,
    @SerializedName("preparation") val preparation: SamplePreparationDto?,
    @SerializedName("microscopeQuality") val microscopeQuality: MicroscopeQualityDto?,
    @SerializedName("metadata") val metadata: SampleMetadataDto,
    @SerializedName("appVersion") val appVersion: String,
    @SerializedName("device") val device: String
)
