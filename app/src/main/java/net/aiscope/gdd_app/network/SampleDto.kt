package net.aiscope.gdd_app.network

import android.os.Build
import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.BuildConfig
import net.aiscope.gdd_app.model.Sample

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