package net.aiscope.gdd_app.ui.mask.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.os.Parcel
import android.os.Parcelable
import android.view.ViewConfiguration
import androidx.core.graphics.withMatrix
import net.aiscope.gdd_app.R
import java.util.*
import kotlin.math.abs

class MaskLayer(
    private val context: Context,
    private val scaleMatrix: Matrix
) {
    companion object {
        private const val TWENTY = 20.0f
        private const val MATRIX_SIZE = 9
        private const val MASK_PAINT_ALPHA = 200

        private fun newDefaultPaintBrush(color: Int) = Paint().apply {
            this.isAntiAlias = true
            this.isDither = true
            this.style = Paint.Style.STROKE
            this.strokeJoin = Paint.Join.ROUND
            this.strokeCap = Paint.Cap.ROUND
            this.color = color
            this.alpha = MASK_PAINT_ALPHA
        }
    }

    private var pathsAndPaints: MutableList<Pair<PointToPointPath, Paint>> = LinkedList()
    private var undoPendingPaths = 0
    private lateinit var currentPath: PointToPointPath
    private lateinit var currentPaint: Paint
    private lateinit var viewDimensions: Pair<Int, Int>
    private lateinit var bitmapDimensions: Pair<Int, Int>

    fun init(width: Int, height: Int) {
        bitmapDimensions = width to height
        scaleBrush()
    }

    fun onDraw(canvas: Canvas) {
        canvas.withMatrix(scaleMatrix) {
            for (i in 0 until pathsAndPaints.size - undoPendingPaths) {
                val (path, paint) = pathsAndPaints[i]
                this.drawPath(path, paint)
            }
        }
    }

    fun onViewSizeChanged(width: Int, height: Int) {
        viewDimensions = width to height
        scaleBrush()
    }

    fun onScaleChanged() = scaleBrush()

    fun undo() = undoPendingPaths++

    fun redo() = undoPendingPaths--

    fun undoAvailable() = (pathsAndPaints.size - undoPendingPaths) > 0

    fun redoAvailable() = undoPendingPaths > 0

    fun getBitmap(): Bitmap {
        val (width, height) = bitmapDimensions
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            onDraw(canvas)
        }
    }

    fun drawStart(x: Float, y: Float) {
        currentPath = PointToPointPath(x, y)
        addPath(currentPath)
    }

    fun drawMove(x: Float, y: Float) {
        val (latestX, latestY) = currentPath.latestPoint
        val dX = abs(x - latestX)
        val dY = abs(y - latestY)
        val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop
        if (dX >= touchTolerance || dY >= touchTolerance) {
            currentPath.quadTo(x, y)
        }
    }

    fun onSaveInstanceState() = CustomViewBaseState(pathsAndPaints)

    fun onRestoreInstanceState(savedState: Parcelable) {
        if (savedState is CustomViewBaseState) {
            pathsAndPaints.addAll(savedState.composePathsAndPaints())
        }
    }

    private fun addPath(path: PointToPointPath) {
        for (i in pathsAndPaints.size - 1 downTo pathsAndPaints.size - undoPendingPaths) {
            pathsAndPaints.removeAt(i)
        }
        undoPendingPaths = 0
        pathsAndPaints.add(path to currentPaint)
    }

    private fun scaleBrush() {
        val scale = getScaleFrom(this.scaleMatrix)

        currentPaint = newDefaultPaintBrush(context.getColor(R.color.colorPrimary)).apply {
            val (viewWidth, viewHeight) = viewDimensions
            strokeWidth = viewHeight.coerceAtLeast(viewWidth).toFloat() / (TWENTY * scale)
        }
    }

    private fun getScaleFrom(m: Matrix): Float {
        val p = FloatArray(MATRIX_SIZE)
        m.getValues(p)

        return p[Matrix.MSCALE_X]
    }

    class CustomViewBaseState(pathsAndPaints: List<Pair<PointToPointPath, Paint>>) : Parcelable {

        companion object CREATOR : Parcelable.Creator<CustomViewBaseState> {
            override fun createFromParcel(parcel: Parcel): CustomViewBaseState {
                return CustomViewBaseState(parcel)
            }

            override fun newArray(size: Int): Array<CustomViewBaseState?> {
                return arrayOfNulls(size)
            }
        }

        val basePathsAndPaintChangesData: MutableList<Pair<PathBaseData, PaintChangeBaseData?>> = LinkedList()

        init {
            val (firstPath, firstPaint) = pathsAndPaints[0]
            basePathsAndPaintChangesData.add(PathBaseData(firstPath.getPoints()) to PaintChangeBaseData(firstPaint.color, firstPaint.strokeWidth))
            var latestPaint: Paint = firstPaint
            for ((path, paint) in pathsAndPaints.subList(1, pathsAndPaints.size)) {
                val pathBaseData = PathBaseData(path.getPoints())
                val paintBaseChangeData = if (paint != latestPaint) PaintChangeBaseData(paint.color, paint.strokeWidth) else null
                basePathsAndPaintChangesData.add(pathBaseData to paintBaseChangeData)
                latestPaint = paint
            }
        }

        constructor(parcel: Parcel) : this(LinkedList<Pair<PointToPointPath, Paint>>().apply {
            parcel.readList(this as List<*>, Pair::class.java.classLoader)
        })

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeList(basePathsAndPaintChangesData as List<*>)
        }

        override fun describeContents() = 0

        fun composePathsAndPaints(): List<Pair<PointToPointPath, Paint>> {
            val result = LinkedList<Pair<PointToPointPath, Paint>>()
            lateinit var latestPaint: Paint
            for ((basePath, paintChange) in basePathsAndPaintChangesData) {
                val path = PointToPointPath(basePath.points)
                latestPaint = if (paintChange == null) latestPaint else newDefaultPaintBrush(paintChange.color).apply { strokeWidth = paintChange.strokeWidth }
                result.add(path to latestPaint)
            }
            return result
        }
    }

    data class PathBaseData(val points: List<Pair<Float, Float>>)
    data class PaintChangeBaseData(val color: Int, val strokeWidth: Float)
}
