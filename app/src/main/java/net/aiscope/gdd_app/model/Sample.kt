package net.aiscope.gdd_app.model

import net.aiscope.gdd_app.extensions.plus
import java.io.File

enum class Status(val id: Short) {
    Incomplete(0), ReadyToUpload(1), Uploaded(2)
}

enum class SmearType(val id: Int) {
    THIN(1),
    THICK(2)
}

@Suppress("MagicNumber") // these are IDs
enum class MalariaSpecies(val id: Int) {
    P_FALCIPARUM(1),
    P_VIVAX(2),
    P_OVALE(3),
    P_MALARIAE(4),
    P_KNOWLESI(5)
}

@Suppress("MagicNumber") // these are IDs
enum class MalariaStage(val id: Int) {
    RING(1),
    TROPHOZOITE(2),
    SCHIZONT(3),
    GAMETOCYTE(4)
}

data class SampleMetadata(
    val smearType: SmearType = SmearType.THIN,
    val species: MalariaSpecies = MalariaSpecies.P_FALCIPARUM,
    val stage: MalariaStage = MalariaStage.RING
)

data class Sample(
    val id: String,
    val healthFacility: String,
    val disease: String? = null,
    val images: LinkedHashSet<File> = linkedSetOf(),
    val masks: LinkedHashSet<File> = linkedSetOf(),
    val metadata: SampleMetadata = SampleMetadata(),
    val status: Status = Status.Incomplete
) {
    fun addImage(path: File) = copy(images = images + path)

    fun addMask(path: File) = copy(masks = masks + path)

    fun nextImageName(): String = "${id}_image_${images.size}"

    fun nextMaskName(): String = "${id}_mask_${images.size}"
}
