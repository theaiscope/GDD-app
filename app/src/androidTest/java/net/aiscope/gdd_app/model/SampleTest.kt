package net.aiscope.gdd_app.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import net.aiscope.gdd_app.network.toDto
import net.aiscope.gdd_app.repository.MicroscopeQualityDto
import net.aiscope.gdd_app.repository.SamplePreparationDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

val VERSION_REGEX = Regex("""^\d+\.\d+\.\d+ \([a-zA-Z0-9]+\)""")

@RunWith(AndroidJUnit4::class)
class SampleTest {

    @Test
    fun shouldFillExpectedFields() {
        val samplePreparation = SamplePreparation(
            WaterType.TAP,
            usesGiemsa = true,
            giemsaFP = false,
            usesPbs = true,
            usesAlcohol = false,
            reusesSlides = true
        )
        val microscopeQuality = MicroscopeQuality(
            true,
            1240
        )
        val sample = Sample("_id",
            "facility",
            "microscopist",
            preparation = samplePreparation,
            microscopeQuality = microscopeQuality
        )

        //This is the DTO for sending to firebase rather than storing locally
        val dto = sample.toDto()

        val expectedSamplePreparationDto = SamplePreparationDto(
            WaterType.TAP.id,
            usesGiemsa = true,
            giemsaFP = false,
            usesPbs = true,
            usesAlcohol = false,
            reusesSlides = true
        )

        val expectedMicroscopeQualityDto = MicroscopeQualityDto(
            true,
            1240
        )

        assertEquals("_id", dto.id)
        assertEquals("facility", dto.healthFacility)
        assertEquals("microscopist", dto.microscopist)
        assertEquals(expectedSamplePreparationDto, dto.preparation)
        assertEquals(expectedMicroscopeQualityDto, dto.microscopeQuality)
        assertTrue(dto.appVersion.matches(VERSION_REGEX))
        assertNotNull(dto.device)
    }
}
