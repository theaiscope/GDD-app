package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.Path
import java.util.*

class PointToPointPath(x: Float, y: Float) : Path() {

    val points: List<Pair<Float, Float>> = LinkedList()
    val firstPoint: Pair<Float, Float> = x to y
    var latestPoint: Pair<Float, Float> = firstPoint

    init {
        moveTo(x, y)
        addPoint(firstPoint)
    }

    constructor(points: List<Pair<Float, Float>>) : this(points[0].first, points[0].second) {
        require(points.size > 1)
        for ((x, y) in points.subList(1, points.size)) {
            quadTo(x, y)
        }
    }

    override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) =
        error("Use quadTo(x, y) instead")

    fun quadTo(xTo: Float, yTo: Float) {
        val (xFrom, yFrom) = latestPoint
        super.quadTo(xFrom, yFrom, xTo, yTo)
        latestPoint = xTo to yTo
        addPoint(latestPoint)
    }

    fun hasMultiplePoints() = points.size > 1

    private fun addPoint(point: Pair<Float, Float>) = (points as MutableList).add(point)

}
