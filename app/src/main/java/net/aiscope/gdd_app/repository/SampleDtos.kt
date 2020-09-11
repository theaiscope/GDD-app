package net.aiscope.gdd_app.repository

import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.extensions.toLinkedHashSet
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.model.SampleStatus
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.model.WaterType
import java.io.File
import java.util.Calendar

data class SampleDto(
    @SerializedName("id") val id: String,
    @SerializedName("healthFacility") val healthFacility: String,
    @SerializedName("microscopist") val microscopist: String,
    @SerializedName("disease") val disease: String,
    @SerializedName("preparation") val preparation: SamplePreparationDto?,
    @SerializedName("microscopeQuality") val microscopeQuality: MicroscopeQualityDto?,
    @SerializedName("imagePaths") val imagePaths: List<String>,
    @SerializedName("maskPaths") val maskPaths: List<String>,
    @SerializedName("metadata") val metadata: SampleMetadataDto,
    @SerializedName("status") val status: Short,
    @SerializedName("createdOn") val createdOn: Calendar = Calendar.getInstance(),
    @SerializedName("lastModified") val lastModified : Calendar = Calendar.getInstance()
) {
    fun toDomain(): Sample = Sample(
        id = id,
        healthFacility = healthFacility,
        microscopist = microscopist,
        disease = disease,
        preparation = preparation?.toDomain(),
        microscopeQuality = microscopeQuality?.toDomain(),
        images = imagePaths.map { File(it) }.toLinkedHashSet(),
        masks = maskPaths.map { File(it) }.toLinkedHashSet(),
        metadata = metadata.toDomain(),
        status = SampleStatus.values().first { it.id == status },
        createdOn = createdOn,
        lastModified = lastModified
    )
}

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

data class MicroscopeQualityDto(
    @SerializedName("isDamaged") val isDamaged: Boolean,
    @SerializedName("magnification") val magnification: Int
) {
    fun toDomain() = MicroscopeQuality(
        isDamaged,
        magnification
    )
}

data class SampleMetadataDto(
    @SerializedName("bloodType") val bloodType: Int,
    @SerializedName("species") val species: Int,
    @SerializedName("comments") val comments: String

) {
    fun toDomain() = SampleMetadata(
        SmearType.values().first { it.id == this.bloodType },
        MalariaSpecies.values().first { it.id == this.species },
        comments
    )
}

fun Sample.toDto() = SampleDto(
    id = id,
    healthFacility = healthFacility,
    microscopist = microscopist,
    disease = disease,
    preparation = preparation?.toDto(),
    microscopeQuality = microscopeQuality?.toDto(),
    imagePaths = images.map { it.absolutePath },
    maskPaths = masks.map { it.absolutePath },
    metadata = metadata.toDto(),
    status = status.id,
    createdOn = createdOn,
    lastModified = lastModified
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
