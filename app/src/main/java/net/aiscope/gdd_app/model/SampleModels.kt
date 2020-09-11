package net.aiscope.gdd_app.model

import net.aiscope.gdd_app.extensions.plus
import java.io.File
import java.util.Calendar
import java.util.LinkedHashSet

data class Sample(
    val id: String,
    val healthFacility: String,
    val microscopist: String,
    val disease: String,
    val preparation: SamplePreparation? = null,
    val microscopeQuality: MicroscopeQuality? = null,
    val images: LinkedHashSet<File> = linkedSetOf(),
    val masks: LinkedHashSet<File> = linkedSetOf(),
    val metadata: SampleMetadata = SampleMetadata(),
    val status: SampleStatus = SampleStatus.Incomplete,
    val createdOn: Calendar = Calendar.getInstance(),
    val lastModified: Calendar = Calendar.getInstance()
) {
    fun addImage(path: File) = copy(images = images + path)

    fun addMask(path: File) = copy(masks = masks + path)

    fun nextImageName(): String = "${id}_image_${images.size}"

    fun nextMaskName(): String = "${id}_mask_${images.size}"
}

enum class SampleStatus(val id: Short) {
    Incomplete(0),
    ReadyToUpload(1),
    Uploaded(2)
}

data class SamplePreparation(
    val waterType: WaterType,
    val usesGiemsa: Boolean,
    val giemsaFP: Boolean,
    val usesPbs: Boolean,
    val usesAlcohol: Boolean,
    val reusesSlides: Boolean
)

@Suppress("MagicNumber") // these are IDs
enum class WaterType(val id: Int) {
    DISTILLED(1),
    BOTTLED(2),
    TAP(3),
    WELL(4)
}

data class MicroscopeQuality(
    val isDamaged: Boolean,
    val magnification: Int
)

data class SampleMetadata(
    val smearType: SmearType = SmearType.THIN,
    val species: MalariaSpecies = MalariaSpecies.P_FALCIPARUM,
    val comments: String = ""
)

@Suppress("MagicNumber") // these are IDs
enum class MalariaSpecies(val id: Int) {
    P_FALCIPARUM(1),
    P_VIVAX(2),
    P_OVALE(3),
    P_MALARIAE(4),
    P_KNOWLESI(5)
}

enum class SmearType(val id: Int) {
    THIN(1),
    THICK(2)
}
