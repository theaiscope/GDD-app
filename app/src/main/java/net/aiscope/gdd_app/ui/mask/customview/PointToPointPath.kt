package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.Path
import java.util.*

class PointToPointPath() : Path() {

    private val points: MutableList<Pair<Float, Float>> = LinkedList()
    lateinit var latestPoint: Pair<Float, Float>

    constructor(x: Float, y: Float) : this() {
        this.points.add(x to y)
        composePathFromPoints()
    }

    constructor(points: List<Pair<Float, Float>>) : this() {
        require(points.size > 1)
        this.points.addAll(points)
        composePathFromPoints()
    }

    override fun quadTo(x1: Float, y1: Float, x2: Float, y2: Float) {
        error("Use quadTo(x, y) instead")
    }

    fun getPoints() = points as List<Pair<Float, Float>>

    fun quadTo(xTo: Float, yTo: Float) {
        val (xFrom, yFrom) = latestPoint
        super.quadTo(xFrom, yFrom, xTo, yTo)
        latestPoint = xTo to yTo
        points.add(latestPoint)
    }

    private fun composePathFromPoints() {
        latestPoint = points[0]
        val (firstX, firstY) = latestPoint
        moveTo(firstX, firstY)
        if (points.size == 1) return
        for (point in points.subList(1, points.size)) {
            val (latestX, latestY) = latestPoint
            val (nextX, nextY) = point
            super.quadTo(latestX, latestY, nextX, nextY)
            latestPoint = point
        }
    }
}
