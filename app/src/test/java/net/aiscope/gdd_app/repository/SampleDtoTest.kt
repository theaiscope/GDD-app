package net.aiscope.gdd_app.repository

import com.nitorcreations.Matchers.containsElements
import net.aiscope.gdd_app.model.BloodQuality
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
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import java.io.File
import java.util.Calendar

class SampleDtoTest {

    @Test
    fun `should map Sample domain model to DTO`() {
        val sampleDTO = sample.toDto()

        assertEquals(ID, sampleDTO.id)
        assertEquals(HEALTH_FACILITY, sampleDTO.healthFacility)
        assertEquals(MICROSCOPIST, sampleDTO.microscopist)
        assertEquals(DISEASE, sampleDTO.disease)
        assertEquals(samplePreparationDto, sampleDTO.preparation)
        assertEquals(microscopeQualityDto, sampleDTO.microscopeQuality)
        assertThat(imagesList, containsElements(sampleDTO.imagePaths))
        assertThat(masksList, containsElements(sampleDTO.maskPaths))
        assertEquals(areMasksEmpty, sampleDTO.areMasksEmpty)
        assertEquals(metadataDto, sampleDTO.metadata)
        assertEquals(status.id, sampleDTO.status)
        assertEquals(createdOn, sampleDTO.createdOn)
        assertEquals(lastModified, sampleDTO.lastModified)
    }

    @Test
    fun `should map Sample DTO to domain model`() {
        val sample = sampleDto.toDomain()

        assertEquals(ID, sample.id)
        assertEquals(HEALTH_FACILITY, sample.healthFacility)
        assertEquals(MICROSCOPIST, sample.microscopist)
        assertEquals(DISEASE, sample.disease)
        assertEquals(samplePreparation, sample.preparation)
        assertEquals(microscopeQuality, sample.microscopeQuality)
        assertEquals(images, sample.captures)
        assertEquals(metadata, sample.metadata)
        assertEquals(status, sample.status)
        assertEquals(createdOn, sample.createdOn)
        assertEquals(lastModified, sample.lastModified)
    }


    companion object {
        private const val COMMENTS = "some-comments"
        private const val DISEASE = "some-disease"
        private const val GIEMSA_FP = true
        private const val HEALTH_FACILITY = "some-health-facility"
        private const val ID = "some-id"
        private const val IS_DAMAGED = true
        private const val MAGNIFICATION = 1
        private const val MICROSCOPIST = "some-microscopist"
        private const val REUSES_SLIDES = true
        private const val USES_GIEMSA = true
        private const val USES_PBS = true
        private const val MASK_1_IS_EMPTY = true
        private const val MASK_2_IS_EMPTY = true

        private val image1: File = createTempFile("image1")
        private val image2: File = createTempFile("image2")
        private val imageInProgress: File = createTempFile("imageInProgress")
        private val mask1: File = createTempFile("mask1")
        private val mask2: File = createTempFile("mask2")
        private val areMasksEmpty: List<Boolean> = listOf(MASK_1_IS_EMPTY, MASK_2_IS_EMPTY)

        private val createdOn: Calendar = Calendar.getInstance()
        private val images = Captures(
            InProgressCapture(imageInProgress), listOf(
                CompletedCapture(image1, mask1, MASK_1_IS_EMPTY),
                CompletedCapture(image2, mask2, MASK_2_IS_EMPTY)
            )
        )
        private val imagesList: List<String> =
            listOf(image1.absolutePath, image2.absolutePath, imageInProgress.absolutePath)
        private val lastModified: Calendar = Calendar.getInstance()
        private val masksList: List<String> = listOf(mask1.absolutePath, mask2.absolutePath)
        private val smearType = SmearType.THIN
        private val species = MalariaSpecies.P_FALCIPARUM
        private val status = SampleStatus.ReadyToUpload
        private val waterType = WaterType.BOTTLED
        private val bloodQuality = BloodQuality.FRESH

        private val metadata = SampleMetadata(smearType, species, COMMENTS)
        private val microscopeQuality = MicroscopeQuality(IS_DAMAGED, MAGNIFICATION)
        private val samplePreparation = SamplePreparation(
            waterType,
            USES_GIEMSA,
            GIEMSA_FP,
            USES_PBS,
            REUSES_SLIDES,
            bloodQuality,
        )

        private val sample = Sample(
            id = ID,
            healthFacility = HEALTH_FACILITY,
            microscopist = MICROSCOPIST,
            disease = DISEASE,
            preparation = samplePreparation,
            microscopeQuality = microscopeQuality,
            captures = images,
            metadata = metadata,
            status = status,
            createdOn = createdOn,
            lastModified = lastModified
        )

        private val samplePreparationDto = SamplePreparationDto(
            waterType = waterType.id,
            usesGiemsa = USES_GIEMSA,
            giemsaFP = GIEMSA_FP,
            usesPbs = USES_PBS,
            reusesSlides = REUSES_SLIDES,
            bloodQuality = bloodQuality.id,
        )

        private val microscopeQualityDto = MicroscopeQualityDto(
            isDamaged = IS_DAMAGED,
            magnification = MAGNIFICATION
        )

        private val metadataDto = SampleMetadataDto(
            bloodType = smearType.id,
            species = species.id,
            comments = COMMENTS
        )

        private val sampleDto = SampleDto(
            id = ID,
            healthFacility = HEALTH_FACILITY,
            microscopist = MICROSCOPIST,
            disease = DISEASE,
            preparation = samplePreparationDto,
            microscopeQuality = microscopeQualityDto,
            imagePaths = imagesList,
            maskPaths = masksList,
            areMasksEmpty = areMasksEmpty,
            metadata = metadataDto,
            status = status.id,
            createdOn = createdOn,
            lastModified = lastModified
        )
    }
}
