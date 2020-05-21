package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import java.util.LinkedList

class MaskCustomViewBaseState(
    pathsPaintsAndStagesNames: List<MaskLayer.PathAndPaint>,
    val undoPendingPaths: Int,
    val currentBrushColor: Int
) : Parcelable {

    companion object CREATOR : Parcelable.Creator<MaskCustomViewBaseState> {
        override fun createFromParcel(parcel: Parcel): MaskCustomViewBaseState {
            return MaskCustomViewBaseState(parcel)
        }

        override fun newArray(size: Int): Array<MaskCustomViewBaseState?> {
            return arrayOfNulls(size)
        }
    }

    internal val basePathsAndPaintChangesData = extractBaseData(pathsPaintsAndStagesNames)

    private fun extractBaseData(inputList: List<MaskLayer.PathAndPaint>): List<BaseData> {
        val result = LinkedList<BaseData>()
        if (inputList.isNotEmpty()) {
            val (firstPath, firstPaint) = inputList[0]
            val firstPathBaseData = PathBaseData(firstPath.points)
            val firstPaintChangeBaseData =
                PaintChangeBaseData(
                    firstPaint.color,
                    firstPaint.strokeWidth,
                    firstPaint.xfermode == MaskLayer.ERASER_XFER_MODE
                )
            result.add(BaseData(firstPathBaseData, firstPaintChangeBaseData))
            var latestPaint: Paint = firstPaint
            for ((path, paint) in inputList.subList(1, inputList.size)) {
                val pathPoints = path.points
                val pathBaseData = PathBaseData(pathPoints)
                val paintBaseChangeData =
                    if (paint == latestPaint)
                        null
                    else
                        PaintChangeBaseData(
                            paint.color,
                            paint.strokeWidth,
                            paint.xfermode == MaskLayer.ERASER_XFER_MODE
                        )
                result.add(BaseData(pathBaseData, paintBaseChangeData))
                latestPaint = paint
            }
        }
        return result
    }

    constructor(parcel: Parcel) : this(
        LinkedList<MaskLayer.PathAndPaint>().apply {
            parcel.readList(this as List<*>, Pair::class.java.classLoader)
        },
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(basePathsAndPaintChangesData as List<*>)
        parcel.writeInt(undoPendingPaths)
        parcel.writeInt(currentBrushColor)
    }

    override fun describeContents() = 0

    fun reassemblePathsPaintsAndStagesNames(): List<MaskLayer.PathAndPaint> {
        val result = LinkedList<MaskLayer.PathAndPaint>()
        lateinit var latestPaint: Paint
        for ((basePath, paintChange) in basePathsAndPaintChangesData) {
            val path = PointToPointPath(basePath.points)
            latestPaint =
                when {
                    paintChange == null -> latestPaint
                    paintChange.isEraser -> MaskLayer.newDefaultPaintEraser(paintChange.strokeWidth)
                    else -> MaskLayer.newDefaultPaintBrush(paintChange.color, paintChange.strokeWidth)
                }
            result.add(MaskLayer.PathAndPaint(path, latestPaint))
        }
        return result
    }

    data class BaseData(
        val pathBaseData: PathBaseData,
        val paintChangeBaseData: PaintChangeBaseData?
    )

    data class PathBaseData(val points: List<Pair<Float, Float>>)

    data class PaintChangeBaseData(val color: Int, val strokeWidth: Float, val isEraser: Boolean)
}
