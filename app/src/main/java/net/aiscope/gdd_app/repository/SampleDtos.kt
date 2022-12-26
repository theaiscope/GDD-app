package net.aiscope.gdd_app.repository

import com.google.gson.annotations.SerializedName
import net.aiscope.gdd_app.model.SampleAge
import net.aiscope.gdd_app.model.CompletedCapture
import net.aiscope.gdd_app.model.Captures
import net.aiscope.gdd_app.model.InProgressCapture
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
    @SerializedName("areMasksEmpty") val areMasksEmpty: List<Boolean>,
    @SerializedName("metadata") val metadata: SampleMetadataDto,
    @SerializedName("status") val status: Short,
    @SerializedName("createdOn") val createdOn: Calendar = Calendar.getInstance(),
    @SerializedName("lastModified") val lastModified: Calendar = Calendar.getInstance()
) {
    fun toDomain(): Sample {
        val areMasksEmpty = backfillAreMaskEmpty()
        val completedCaptures = buildCompletedCaptures(areMasksEmpty)
        val inProgressCapture = extractInProgressCapture()

        return Sample(
            id = id,
            healthFacility = healthFacility,
            microscopist = microscopist,
            disease = disease,
            preparation = preparation?.toDomain(),
            microscopeQuality = microscopeQuality?.toDomain(),
            captures = Captures(inProgressCapture, completedCaptures),
            metadata = metadata.toDomain(),
            status = SampleStatus.values().first { it.id == status },
            createdOn = createdOn,
            lastModified = lastModified
        )
    }

    private fun backfillAreMaskEmpty() =
        areMasksEmpty.ifEmpty { List(imagePaths.size) { false } }

    private fun buildCompletedCaptures(areMasksEmpty: List<Boolean>) =
        imagePaths.zip(maskPaths).zip(areMasksEmpty) { filesPair, emptyMask ->
            CompletedCapture(File(filesPair.first), File(filesPair.second), emptyMask)
        }

    private fun extractInProgressCapture() =
        if (imagePaths.size > maskPaths.size) InProgressCapture(File(imagePaths.last())) else null
}

data class SamplePreparationDto(
    @SerializedName("waterType") val waterType: Int,
    @SerializedName("usesGiemsa") val usesGiemsa: Boolean,
    @SerializedName("giemsaFP") val giemsaFP: Boolean,
    @SerializedName("usesPbs") val usesPbs: Boolean,
    @SerializedName("reusesSlides") val reusesSlides: Boolean,
    @SerializedName("sampleAge") val sampleAge: String
) {
    fun toDomain() = SamplePreparation(
        WaterType.values().first { it.id == waterType },
        usesGiemsa,
        giemsaFP,
        usesPbs,
        reusesSlides,
        SampleAge.values().first { it.id == sampleAge }
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
    imagePaths = captures.completedCaptures.map { it.image.absolutePath } +
            listOfNotNull(captures.inProgressCapture?.image?.absolutePath),
    maskPaths = captures.completedCaptures.map { it.mask.absolutePath },
    areMasksEmpty = captures.completedCaptures.map { it.maskIsEmpty },
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
    reusesSlides,
    sampleAge.id,
)

fun MicroscopeQuality.toDto() = MicroscopeQualityDto(
    isDamaged,
    magnification
)

fun SampleMetadata.toDto() = SampleMetadataDto(smearType.id, species.id, comments)
