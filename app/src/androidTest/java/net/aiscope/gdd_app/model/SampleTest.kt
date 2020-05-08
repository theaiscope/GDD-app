package net.aiscope.gdd_app.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import net.aiscope.gdd_app.network.toDto
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
        val sample = Sample("_id",
            "facility",
            "microscopist")

        //This is the DTO for sending to firebase rather than storing locally
        val dto = sample.toDto()

        assertEquals(dto.id, "_id")
        assertEquals(dto.healthFacility,"facility")
        assertEquals(dto.microscopist, "microscopist")
        assertTrue(dto.appVersion.matches(VERSION_REGEX))
        assertNotNull(dto.device)
    }
}
