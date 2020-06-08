package net.aiscope.gdd_app.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import net.aiscope.gdd_app.network.MicroscopeQualityDto
import net.aiscope.gdd_app.network.SamplePreparationDto
import net.aiscope.gdd_app.network.toDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SampleTest {

    companion object {
        private const val ID = "_id"
        private const val FACILITY = "facility"
        private const val MICROSCOPIST = "microscopist"

        private val VERSION_REGEX = Regex("""^\d+\.\d+\.\d+ \([a-zA-Z0-9]+\)""")
        private val ISO_REGEX = Regex("""^\d{4}-[01]\d-[0-3]\dT[0-2]\d:[0-5]\d:[0-5]\d([+-][0-2]\d[0-5]\d|Z)""")

        private val WATER_TYPE = WaterType.TAP
        private const val USES_GIEMSA = true
        private const val GIEMSA_FP = true
        private const val USES_PBS = true
        private const val USES_ALCOHOL = true
        private const val REUSES_SLIDES = true

        private const val IS_DAMAGED = true
        private const val MAGNIFICATION = 1240
    }

    @Test
    fun shouldFillExpectedFields() {
        val samplePreparation = SamplePreparation(
            WATER_TYPE,
            USES_GIEMSA,
            GIEMSA_FP,
            USES_PBS,
            USES_ALCOHOL,
            REUSES_SLIDES
        )
        val microscopeQuality = MicroscopeQuality(IS_DAMAGED, MAGNIFICATION)
        val sample = Sample(
            ID,
            FACILITY,
            MICROSCOPIST,
            preparation = samplePreparation,
            microscopeQuality = microscopeQuality
        )

        //This is the DTO for sending to firebase rather than storing locally
        val dto = sample.toDto()

        val expectedSamplePreparationDto = SamplePreparationDto(
            WATER_TYPE.id,
            USES_GIEMSA,
            GIEMSA_FP,
            USES_PBS,
            USES_ALCOHOL,
            REUSES_SLIDES
        )

        val expectedMicroscopeQualityDto = MicroscopeQualityDto(IS_DAMAGED, MAGNIFICATION)

        assertEquals(ID, dto.id)
        assertEquals(FACILITY, dto.healthFacility)
        assertEquals(MICROSCOPIST, dto.microscopist)
        assertEquals(expectedSamplePreparationDto, dto.preparation)
        assertEquals(expectedMicroscopeQualityDto, dto.microscopeQuality)
        assertTrue(dto.appVersion.matches(VERSION_REGEX))
        assertTrue(dto.lastModified.matches(ISO_REGEX))
        assertNotNull(dto.device)
    }
}
