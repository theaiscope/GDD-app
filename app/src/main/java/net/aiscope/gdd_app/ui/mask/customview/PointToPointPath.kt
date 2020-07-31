package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.Path
import android.graphics.PointF
import android.os.Parcelable
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import kotlinx.android.parcel.Parcelize

@Parcelize
@Suppress("DataClassPrivateConstructor")
data class PointToPointPath internal constructor(private val points: MutableList<PointF>) : Path(), Parcelable {

    companion object {
        fun createWithStartingPoint(point: PointF) = PointToPointPath(mutableListOf(point))
        fun createFromPoints(points: List<PointF>) = PointToPointPath(points.toMutableList())
    }

    init {
        if (points.isEmpty()) {
            error("`points` cannot be empty")
        }
        moveTo(points[0].x, points[0].y)
        for ((x, y) in points.subList(1, points.size)) {
            superQuadTo(x, y)
        }
    }

    override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) =
        error("Use quadTo(x, y) instead")

    fun quadTo(xTo: Float, yTo: Float) {
        superQuadTo(xTo, yTo)
        addPoint(PointF(xTo, yTo))
    }

    private fun superQuadTo(xTo: Float, yTo: Float) {
        val latestPoint = points.last()
        val (xFrom, yFrom) = latestPoint
        super.quadTo(xFrom, yFrom, xTo, yTo)
    }

    fun hasMultiplePoints() = points.size > 1

    fun firstPoint() = points.first()

    private fun addPoint(point: PointF) = points.add(point)

}
