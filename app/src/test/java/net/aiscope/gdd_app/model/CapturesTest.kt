package net.aiscope.gdd_app.model

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class CapturesTest {

    companion object {
        private const val MASK_1_IS_EMPTY = true
        private const val MASK_2_IS_EMPTY = true

        private val image1: File = createTempFile("image1")
        private val image2: File = createTempFile("image2")
        private val mask1: File = createTempFile("mask1")
        private val mask2: File = createTempFile("mask2")
    }

    @Test(expected = IllegalStateException::class)
    fun `should throw exception when upserting existing mask and there is a capture in progress`() {
        val captures = Captures(
            InProgressCapture(image2),
            listOf(CompletedCapture(image1, mask1, MASK_1_IS_EMPTY))
        )

        captures.upsertMask(mask1, MASK_1_IS_EMPTY)
    }

    @Test
    fun `should complete in progress capture when upserting new mask`() {
        val captures = Captures(
            InProgressCapture(image2),
            listOf(CompletedCapture(image1, mask1, MASK_1_IS_EMPTY))
        )

        val result = captures.upsertMask(mask2, MASK_2_IS_EMPTY)
        val expected = Captures(
            null,
            listOf(
                CompletedCapture(image1, mask1, MASK_1_IS_EMPTY),
                CompletedCapture(image2, mask2, MASK_2_IS_EMPTY)
            )
        )

        assertEquals(expected, result)
    }

    @Test
    fun `should update existing mask when upserting and there is no in progress capture`() {
        val captures = Captures(
            null,
            listOf(
                CompletedCapture(image1, mask1, MASK_1_IS_EMPTY),
                CompletedCapture(image2, mask2, MASK_2_IS_EMPTY)
            )
        )

        val result = captures.upsertMask(mask2, !MASK_2_IS_EMPTY)
        val expected = Captures(
            null,
            listOf(
                CompletedCapture(image1, mask1, MASK_1_IS_EMPTY),
                CompletedCapture(image2, mask2, !MASK_2_IS_EMPTY)
            )
        )

        assertEquals(expected, result)
    }
}
