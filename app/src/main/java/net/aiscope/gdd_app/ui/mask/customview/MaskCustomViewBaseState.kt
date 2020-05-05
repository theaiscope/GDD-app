package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import net.aiscope.gdd_app.ui.mask.BrushDiseaseStage
import java.util.*

class MaskCustomViewBaseState(
    pathsPaintsAndStagesNames: List<MaskLayer.PathPaintAndStageName>,
    val undoPendingPaths: Int,
    val currentBrushDiseaseStage: BrushDiseaseStage
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

    private fun extractBaseData(inputList: List<MaskLayer.PathPaintAndStageName>): List<BaseData> {
        val result = LinkedList<BaseData>()
        if (inputList.isNotEmpty()) {
            val (firstPath, firstPaint, firstStageName) = inputList[0]
            val firstPathBaseData = PathBaseData(firstPath.points)
            val firstPaintChangeBaseData =
                PaintChangeBaseData(firstPaint.color, firstPaint.strokeWidth)
            result.add(BaseData(firstPathBaseData, firstPaintChangeBaseData, firstStageName))
            var latestPaint: Paint = firstPaint
            for ((path, paint, stageName) in inputList.subList(1, inputList.size)) {
                val pathPoints = path.points
                val pathBaseData = PathBaseData(pathPoints)
                val paintBaseChangeData =
                    if (paint != latestPaint)
                        PaintChangeBaseData(paint.color, paint.strokeWidth)
                    else
                        null
                result.add(BaseData(pathBaseData, paintBaseChangeData, stageName))
                latestPaint = paint
            }
        }
        return result
    }

    constructor(parcel: Parcel) : this(
        LinkedList<MaskLayer.PathPaintAndStageName>().apply {
            parcel.readList(this as List<*>, Pair::class.java.classLoader)
        },
        parcel.readInt(),
        parcel.readTypedObject(BrushDiseaseStage.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeList(basePathsAndPaintChangesData as List<*>)
        parcel.writeInt(undoPendingPaths)
        parcel.writeTypedObject(currentBrushDiseaseStage, flags)
    }

    override fun describeContents() = 0

    fun reassemblePathsPaintsAndStagesNames(): List<MaskLayer.PathPaintAndStageName> {
        val result = LinkedList<MaskLayer.PathPaintAndStageName>()
        lateinit var latestPaint: Paint
        for ((basePath, paintChange, stageName) in basePathsAndPaintChangesData) {
            val path = PointToPointPath(basePath.points)
            latestPaint =
                if (paintChange == null)
                    latestPaint
                else
                    MaskLayer.newDefaultPaintBrush(paintChange.color, paintChange.strokeWidth)
            result.add(MaskLayer.PathPaintAndStageName(path, latestPaint, stageName))
        }
        return result
    }

    data class BaseData(
        val pathBaseData: PathBaseData,
        val paintChangeBaseData: PaintChangeBaseData?,
        val diseaseStageName: String
    )

    data class PathBaseData(val points: List<Pair<Float, Float>>)

    data class PaintChangeBaseData(val color: Int, val strokeWidth: Float)
}
