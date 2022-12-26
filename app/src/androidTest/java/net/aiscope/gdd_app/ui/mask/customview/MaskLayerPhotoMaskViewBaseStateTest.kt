package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.Color
import android.graphics.PointF
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import net.aiscope.gdd_app.test.extensions.testParcel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class MaskLayerPhotoMaskViewBaseStateTest {

    @Test
    fun itShouldRestoreProperly() {
        val state = givenPhotoMaskViewBaseState()

        state.testParcel().apply {
            assertEquals(state, this)
            assertNotSame(state, this)
        }
    }

    private fun givenPhotoMaskViewBaseState(): PhotoMaskViewBaseState {
        val points1 = listOf(PointF(0f, 1f), PointF(2f, 3f), PointF(4f, 5f))
        val points2 = listOf(PointF(1f, 2f), PointF(3f, 4f))
        val points3 = listOf(PointF(2f, 3f), PointF(4f, 5f), PointF(4f, 5f), PointF(6f, 7f))
        val paint1 = buildPaint(Color.BLUE, 5f)
        val paint2 = buildPaint(Color.YELLOW, 3f, eraser = true)
        val paint3 = buildPaint(Color.RED, 1f)

        val input = listOf(
            PathAndPaint(PointToPointPath.createFromPoints(points1), paint1),
            PathAndPaint(PointToPointPath.createFromPoints(points2), paint1),
            PathAndPaint(PointToPointPath.createFromPoints(points3), paint1),
            PathAndPaint(PointToPointPath.createFromPoints(points2), paint2),
            PathAndPaint(PointToPointPath.createFromPoints(points3), paint2),
            PathAndPaint(PointToPointPath.createFromPoints(points2), paint3),
            PathAndPaint(PointToPointPath.createFromPoints(points3), paint3)
        )
        return PhotoMaskViewBaseState(input, 1, 2)
    }

    private fun buildPaint(color: Int, strokeWidth: Float, eraser: Boolean = false) =
        when (eraser) {
            false -> PaintFactory.newDefaultPaintBrush(color, strokeWidth)
            true -> PaintFactory.newDefaultPaintEraser(strokeWidth)
        }
}
