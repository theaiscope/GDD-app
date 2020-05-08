package net.aiscope.gdd_app.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import net.aiscope.gdd_app.network.toDto
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SampleTest {

    @Test
    fun shouldFillExpectedFields() {
        val sample = Sample("_id",
            "facility",
            "microscopist")

        //This is the DTO for sending to firebase rather than storing locally
        val dto = sample.toDto()

        Assert.assertEquals(dto.id, "_id")
        Assert.assertEquals(dto.healthFacility,"facility")
        Assert.assertEquals(dto.microscopist, "microscopist")
        Assert.assertTrue(dto.appVersion.matches(Regex("""^\d+\.\d+\.\d+ \([a-zA-Z0-9]+\)""")))
        Assert.assertNotNull(dto.device)
    }
}
