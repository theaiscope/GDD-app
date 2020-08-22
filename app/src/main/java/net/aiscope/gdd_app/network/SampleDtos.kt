package net.aiscope.gdd_app.network

import android.os.Build
import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.BuildConfig
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SamplePreparation
import java.text.SimpleDateFormat

val ISO_FORMAT = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

data class SampleDto(
    @SerializedName("id") val id: String,
    @SerializedName("healthFacility") val healthFacility: String,
    @SerializedName("microscopist") val microscopist: String,
    @SerializedName("disease") val disease: String,
    @SerializedName("preparation") val preparation: SamplePreparationDto?,
    @SerializedName("microscopeQuality") val microscopeQuality: MicroscopeQualityDto?,
    @SerializedName("metadata") val metadata: SampleMetadataDto,
    @SerializedName("appVersion") val appVersion: String,
    @SerializedName("device") val device: String,
    @SerializedName("createdOn") val createdOn: String,
    @SerializedName("lastModified") val lastModified: String
)

data class SamplePreparationDto(
    @SerializedName("waterType") val waterType: Int,
    @SerializedName("usesGiemsa") val usesGiemsa: Boolean,
    @SerializedName("giemsaFP") val giemsaFP: Boolean,
    @SerializedName("usesPbs") val usesPbs: Boolean,
    @SerializedName("usesAlcohol") val usesAlcohol: Boolean,
    @SerializedName("reusesSlides") val reusesSlides: Boolean
)

data class MicroscopeQualityDto(
    @SerializedName("isDamaged") val isDamaged: Boolean,
    @SerializedName("magnification") val magnification: Int
)

data class SampleMetadataDto(
    @SerializedName("bloodType") val bloodType: Int,
    @SerializedName("species") val species: Int,
    @SerializedName("comments") val comments: String
)

fun Sample.toDto() = SampleDto(
    id = id,
    healthFacility = healthFacility,
    microscopist = microscopist,
    disease = disease ?: "",
    preparation = preparation?.toDto(),
    microscopeQuality = microscopeQuality?.toDto(),
    metadata = metadata.toDto(),
    appVersion = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
    device = "${Build.MANUFACTURER} ${Build.MODEL}",
    createdOn = ISO_FORMAT.format(createdOn.time),
    lastModified = ISO_FORMAT.format(lastModified.time)
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

fun SampleMetadata.toDto() = SampleMetadataDto(smearType.id, species.id, comments)
