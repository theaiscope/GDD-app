package net.aiscope.gdd_app.ui.mask.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.os.Parcelable
import android.view.ViewConfiguration
import androidx.core.graphics.withMatrix
import net.aiscope.gdd_app.ui.mask.BrushDiseaseStage
import java.util.*
import kotlin.math.abs

@Suppress("TooManyFunctions")
class MaskLayer(
    private val context: Context,
    private val scaleMatrix: Matrix
) {
    companion object {
        private const val MATRIX_SIZE = 9
        private const val MASK_PAINT_ALPHA = 0xCC
        private const val PATH_STROKE = 80f
        private const val TEXT_SIZE = 48f
        private const val TEXT_STROKE = 12f
        private const val ALPHA_OPAQUE = 0xFF
        private const val VERTICAL_PADDING_PX = 4

        private val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = TEXT_SIZE
        }
        private val textStrokePaint = Paint().apply {
            color = Color.DKGRAY
            style = Paint.Style.STROKE
            textSize = TEXT_SIZE
            strokeWidth = TEXT_STROKE
        }

        fun newDefaultPaintBrush(color: Int, strokeWidth: Float) = Paint().apply {
            this.isAntiAlias = true
            this.isDither = true
            this.style = Paint.Style.STROKE
            this.strokeJoin = Paint.Join.ROUND
            this.strokeCap = Paint.Cap.ROUND
            this.color = color
            this.alpha = MASK_PAINT_ALPHA
            this.strokeWidth = strokeWidth
        }
    }

    private var pathsPaintsAndStagesNames: MutableList<PathPaintAndStageName> = LinkedList()
    private var undoPendingPaths = 0
    private lateinit var currentPath: PointToPointPath
    private var currentPaintDirty = false
    var brushDiseaseStage = BrushDiseaseStage(0, "non-initialized", 0)
        set(value) {
            currentPaintDirty = currentPaintDirty || field != value
            field = value
        }
    private var currentStrokeWidth = PATH_STROKE
        set(value) {
            currentPaintDirty = currentPaintDirty || field != value
            field = value
        }
    private var currentPaint = Paint()
        get() {
            if (currentPaintDirty) cleanCurrentPaint()
            return field
        }
    private lateinit var viewDimensions: Pair<Int, Int>
    private lateinit var bitmapDimensions: Pair<Int, Int>

    fun init(width: Int, height: Int) {
        bitmapDimensions = width to height
        scaleBrushAndTextPaints()
    }

    fun onDraw(canvas: Canvas) {
        canvas.withMatrix(scaleMatrix) {
            drawPaths(this)
        }
    }

    private fun drawPaths(
        canvas: Canvas,
        drawStageNames: Boolean = false,
        removeAlpha: Boolean = false
    ) {
        for (i in 0 until pathsPaintsAndStagesNames.size - undoPendingPaths) {
            val (path, paint, stageName) = pathsPaintsAndStagesNames[i]
            val paintReviewed =
                if (removeAlpha) Paint().apply { set(paint); alpha = ALPHA_OPAQUE } else paint
            canvas.drawPath(path, paintReviewed)
            if (drawStageNames) drawPathText(path, paint, stageName, canvas)
        }
    }

    private fun drawPathText(
        path: PointToPointPath,
        pathPaint: Paint,
        text: String,
        canvas: Canvas
    ) {
        val textBoundsRuler = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBoundsRuler)

        val currentScale = currentScale()
        val (firstPointX, firstPointY) = path.firstPoint
        val textX = firstPointX - textBoundsRuler.width() / 2
        val verticalPadding = pxToDp(VERTICAL_PADDING_PX * 2) / currentScale
        val textY = firstPointY +
                if (path.verticalDirection > 0) {
                    -(pathPaint.strokeWidth / 2 + verticalPadding)
                } else {
                    textBoundsRuler.height() + pathPaint.strokeWidth / 2 +
                            verticalPadding - pxToDp(VERTICAL_PADDING_PX) / currentScale
                }

        canvas.drawText(text, textX, textY, textStrokePaint)
        canvas.drawText(text, textX, textY, textPaint)
    }

    private fun pxToDp(px: Int) = (px * context.resources.displayMetrics.density).toInt()

    fun onViewSizeChanged(width: Int, height: Int) {
        viewDimensions = width to height
        scaleBrushAndTextPaints()
    }

    fun onScaleChanged() = scaleBrushAndTextPaints()

    fun undo() = undoPendingPaths++

    fun redo() = undoPendingPaths--

    fun undoAvailable() = (pathsPaintsAndStagesNames.size - undoPendingPaths) > 0

    fun redoAvailable() = undoPendingPaths > 0

    fun getBitmap(): Bitmap {
        val (width, height) = bitmapDimensions
        return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            drawPaths(canvas, removeAlpha = true)
        }
    }

    fun drawStart(x: Float, y: Float) {
        currentPath = PointToPointPath(x, y)
    }

    fun drawMove(x: Float, y: Float) {
        val (latestX, latestY) = currentPath.latestPoint
        val dX = abs(x - latestX)
        val dY = abs(y - latestY)
        val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop / currentScale()
        if (dX >= touchTolerance || dY >= touchTolerance) {
            currentPath.quadTo(x, y)
            if (pathsPaintsAndStagesNames.isEmpty() || currentPath != pathsPaintsAndStagesNames.last().path) {
                addPath(currentPath)
            }
        }
    }

    fun onSaveInstanceState() =
        MaskCustomViewBaseState(pathsPaintsAndStagesNames, undoPendingPaths, brushDiseaseStage)

    fun onRestoreInstanceState(savedState: Parcelable) {
        if (savedState is MaskCustomViewBaseState) {
            undoPendingPaths = savedState.undoPendingPaths
            pathsPaintsAndStagesNames.addAll(savedState.reassemblePathsPaintsAndStagesNames())
            brushDiseaseStage = savedState.currentBrushDiseaseStage
        }
    }

    private fun addPath(path: PointToPointPath) {
        for (i in pathsPaintsAndStagesNames.size - 1 downTo pathsPaintsAndStagesNames.size - undoPendingPaths) {
            pathsPaintsAndStagesNames.removeAt(i)
        }
        undoPendingPaths = 0
        pathsPaintsAndStagesNames.add(
            PathPaintAndStageName(
                path,
                currentPaint,
                brushDiseaseStage.name
            )
        )
    }

    private fun scaleBrushAndTextPaints() {
        val currentScale = currentScale()
        currentStrokeWidth = PATH_STROKE / currentScale

        textPaint.textSize = TEXT_SIZE / currentScale
        textStrokePaint.textSize = textPaint.textSize
        textStrokePaint.strokeWidth = TEXT_STROKE / currentScale
    }

    private fun currentScale() =
        FloatArray(MATRIX_SIZE).apply {
            scaleMatrix.getValues(this)
        }[Matrix.MSCALE_X]

    private fun cleanCurrentPaint() {
        currentPaint = newDefaultPaintBrush(brushDiseaseStage.maskColor, currentStrokeWidth)
    }

    data class PathPaintAndStageName(
        val path: PointToPointPath,
        val paint: Paint,
        val diseaseStageName: String
    )
}
