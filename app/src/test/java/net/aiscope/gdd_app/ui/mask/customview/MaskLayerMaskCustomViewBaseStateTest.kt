package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.Paint
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import net.aiscope.gdd_app.ui.mask.BrushDiseaseStage
import net.aiscope.gdd_app.ui.mask.customview.MaskCustomViewBaseState.BaseData
import net.aiscope.gdd_app.ui.mask.customview.MaskCustomViewBaseState.PaintChangeBaseData
import net.aiscope.gdd_app.ui.mask.customview.MaskCustomViewBaseState.PathBaseData
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test

class MaskLayerMaskCustomViewBaseStateTest {

    @Test
    fun itShouldNotRepeatSamePaintOnCreation() {
        val points1 = listOf(0f to 1f, 2f to 3f, 4f to 5f)
        val points2 = listOf(1f to 2f, 3f to 4f)
        val points3 = listOf(2f to 3f, 4f to 5f, 4f to 5f, 6f to 7f)
        val paint1 = mockPaint(1, 5f)
        val paint2 = mockPaint(2, 3f, true)
        val paint3 = mockPaint(2, 1f)
        val paintChangeBaseData1 = PaintChangeBaseData(paint1.color, paint1.strokeWidth, false)
        val paintChangeBaseData2 = PaintChangeBaseData(paint2.color, paint2.strokeWidth, true)
        val paintChangeBaseData3 = PaintChangeBaseData(paint3.color, paint3.strokeWidth, false)

        val input = listOf(
            MaskLayer.PathAndPaint(mockPointToPointPath(points1), paint1),
            MaskLayer.PathAndPaint(mockPointToPointPath(points2), paint1),
            MaskLayer.PathAndPaint(mockPointToPointPath(points3), paint1),
            MaskLayer.PathAndPaint(mockPointToPointPath(points2), paint2),
            MaskLayer.PathAndPaint(mockPointToPointPath(points3), paint2),
            MaskLayer.PathAndPaint(mockPointToPointPath(points2), paint3),
            MaskLayer.PathAndPaint(mockPointToPointPath(points3), paint3)
        )
        val customViewBaseState =
            MaskCustomViewBaseState(input, 0, BrushDiseaseStage(0, "Some disease", 0))
        val actualOutput = customViewBaseState.basePathsAndPaintChangesData
        val expectedOutput = listOf(
            BaseData(PathBaseData(points1), paintChangeBaseData1),
            BaseData(PathBaseData(points2), null),
            BaseData(PathBaseData(points3), null),
            BaseData(PathBaseData(points2), paintChangeBaseData2),
            BaseData(PathBaseData(points3), null),
            BaseData(PathBaseData(points2), paintChangeBaseData3),
            BaseData(PathBaseData(points3), null)
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
            whenever(this.points).thenReturn(points)
        }

    private fun mockPaint(color: Int, strokeWidth: Float, eraser: Boolean = false) =
        mock<Paint>().apply {
            whenever(this.color).thenReturn(color)
            whenever(this.strokeWidth).thenReturn(strokeWidth)
            whenever(this.xfermode).thenReturn(if (eraser) MaskLayer.ERASER_XFER_MODE else null)
        }
}