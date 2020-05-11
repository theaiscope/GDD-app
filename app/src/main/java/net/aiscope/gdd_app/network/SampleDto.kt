package net.aiscope.gdd_app.network

import android.os.Build
import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.BuildConfig
import net.aiscope.gdd_app.model.Sample

data class SampleMetadataDto(
    @SerializedName("bloodType") val bloodType: Int,
    @SerializedName("species") val species: Int
)

data class SampleDto(
    @SerializedName("id") val id: String,
    @SerializedName("healthFacility") val healthFacility: String,
    @SerializedName("microscopist") val microscopist: String,
    @SerializedName("disease") val disease: String,
    @SerializedName("metadata") val metadata: SampleMetadataDto,
    @SerializedName("appVersion") val appVersion: String,
    @SerializedName("device") val device: String
)

fun Sample.toDto() = SampleDto(
    id = id,
    healthFacility = healthFacility,
    microscopist = microscopist,
    disease = disease ?: "",
    metadata = SampleMetadataDto(
        bloodType = metadata.smearType.id,
        species = metadata.species.id
    ),
    appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
    device = "${Build.MANUFACTURER} ${Build.MODEL}"
)
