package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.Paint
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

class MaskLayerCustomViewBaseStateTest {

    @Test
    fun itShouldNotRepeatSamePaintOnCreation() {
        val points1 = listOf(0f to 1f, 2f to 3f, 4f to 5f)
        val points2 = listOf(1f to 2f, 3f to 4f)
        val points3 = listOf(2f to 3f, 4f to 5f, 4f to 5f, 6f to 7f)
        val paint1 = mockPaint(1, 5f)
        val paint2 = mockPaint(2, 1f)
        val input = listOf(
            mockPointToPointPath(points1) to paint1,
            mockPointToPointPath(points2) to paint1,
            mockPointToPointPath(points3) to paint1,
            mockPointToPointPath(points2) to paint2,
            mockPointToPointPath(points3) to paint2
        )
        val customViewBaseState = MaskLayer.CustomViewBaseState(input, 0)
        val actualOutput = customViewBaseState.basePathsAndPaintChangesData

        val expectedOutput = listOf(
            MaskLayer.PathBaseData(points1) to MaskLayer.PaintChangeBaseData(
                paint1.color,
                paint1.strokeWidth
            ),
            MaskLayer.PathBaseData(points2) to null,
            MaskLayer.PathBaseData(points3) to null,
            MaskLayer.PathBaseData(points2) to MaskLayer.PaintChangeBaseData(
                paint2.color,
                paint2.strokeWidth
            ),
            MaskLayer.PathBaseData(points3) to null
        )

        assertEquals(expectedOutput, actualOutput)
    }

    @Test
    @Ignore("Can't test right now")
    fun itShouldRestoreProperly() {
        TODO("PointToPointPath's constructor uses Path's methods and I couldn't mock those (the difficulty is that those methods are used in the constructor)")
    }

    private fun mockPointToPointPath(points: List<Pair<Float, Float>>) =
        mock<PointToPointPath>().apply {
            whenever(this.getPoints()).thenReturn(points)
        }

    private fun mockPaint(color: Int, strokeWidth: Float) =
        mock<Paint>().apply {
            whenever(this.color).thenReturn(color)
            whenever(this.strokeWidth).thenReturn(strokeWidth)
        }
}