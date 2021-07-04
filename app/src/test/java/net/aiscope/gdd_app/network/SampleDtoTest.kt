package net.aiscope.gdd_app.network

import net.aiscope.gdd_app.model.BloodQuality
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.model.SampleStatus
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.model.WaterType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class SampleDtoTest {

    companion object {
        private val VERSION_REGEX = Regex("""^\d+\.\d+\.\d+ \([a-zA-Z0-9]+\)""")
        private val ISO_REGEX =
            Regex("""^\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d([+-][0-2]\d[0-5]\d|Z)""")

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

        private val createdOn: Calendar = Calendar.getInstance()
        private val lastModified: Calendar = Calendar.getInstance()
        private val smearType = SmearType.THIN
        private val species = MalariaSpecies.P_FALCIPARUM
        private val status = SampleStatus.ReadyToUpload
        private val waterType = WaterType.BOTTLED
        private val bloodQuality = BloodQuality.OLD

        val metadata = SampleMetadata(smearType, species, COMMENTS)
        val microscopeQuality = MicroscopeQuality(IS_DAMAGED, MAGNIFICATION)
        val samplePreparation = SamplePreparation(
            waterType = waterType,
            usesGiemsa = USES_GIEMSA,
            giemsaFP = GIEMSA_FP,
            usesPbs = USES_PBS,
            reusesSlides = REUSES_SLIDES,
            bloodQuality = bloodQuality
        )

        val sample = Sample(
            id = ID,
            healthFacility = HEALTH_FACILITY,
            microscopist = MICROSCOPIST,
            disease = DISEASE,
            preparation = samplePreparation,
            microscopeQuality = microscopeQuality,
            metadata = metadata,
            status = status,
            createdOn = createdOn,
            lastModified = lastModified
        )
    }

    @Test
    fun `should map Sample model to DTO`() {
        val sampleDTO = sample.toDto()

        assertEquals(sample.id, sampleDTO.id)
        assertEquals(sample.healthFacility, sampleDTO.healthFacility)
        assertEquals(sample.microscopist, sampleDTO.microscopist)
        assertEquals(sample.disease, sampleDTO.disease)
        assertEquals(sample.preparation?.toDto(), sampleDTO.preparation)
        assertEquals(sample.microscopeQuality?.toDto(), sampleDTO.microscopeQuality)
        assertEquals(sample.metadata.toDto(), sampleDTO.metadata)
        assertTrue(sampleDTO.appVersion.matches(VERSION_REGEX))
        assertNotNull(sampleDTO.device)
        assertTrue(sampleDTO.createdOn.matches(ISO_REGEX))
        assertTrue(sampleDTO.lastModified.matches(ISO_REGEX))
    }

    @Test
    fun `should map SamplePreparation model to DTO`() {
        val samplePreparationDTO = samplePreparation.toDto()

        assertEquals(samplePreparation.waterType.id, samplePreparationDTO.waterType)
        assertEquals(samplePreparation.usesGiemsa, samplePreparationDTO.usesGiemsa)
        assertEquals(samplePreparation.giemsaFP, samplePreparationDTO.giemsaFP)
        assertEquals(samplePreparation.usesPbs, samplePreparationDTO.usesPbs)
        assertEquals(samplePreparation.reusesSlides, samplePreparationDTO.reusesSlides)
        assertEquals(samplePreparation.bloodQuality.id, samplePreparationDTO.bloodQuality)
    }

    @Test
    fun `should map MicroscopeQuality model to DTO`() {
        val microscopeQualityDTO = microscopeQuality.toDto()

        assertEquals(microscopeQuality.isDamaged, microscopeQualityDTO.isDamaged)
        assertEquals(microscopeQuality.magnification, microscopeQualityDTO.magnification)
    }

    @Test
    fun `should map SampleMetadata model to DTO`() {
        val metadataDTO = metadata.toDto()

        assertEquals(metadata.smearType.id, metadataDTO.bloodType)
        assertEquals(metadata.species.id, metadataDTO.species)
        assertEquals(metadata.comments, metadataDTO.comments)
    }
}