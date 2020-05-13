package net.aiscope.gdd_app.ui.mask.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.os.Parcelable
import android.view.ViewConfiguration
import androidx.core.graphics.withMatrix
import net.aiscope.gdd_app.ui.mask.BrushDiseaseStage
import java.util.*
import kotlin.math.abs

@Suppress("TooManyFunctions")
//TODO("Rename to MaskLayerController")
class MaskLayer(
    private val context: Context,
    private val imageMatrix: Matrix
) {
    companion object {
        private const val ALPHA_OPAQUE = 0xFF
        private const val MASK_PAINT_ALPHA = .8
        private const val PATH_STROKE_WIDTH = 80f
        private val BITMAP_TRANSFER_PAINT = Paint()
        val ERASER_XFER_MODE = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        fun newDefaultPaintBrush(color: Int, strokeWidth: Float) = Paint().apply {
            this.isAntiAlias = true
            this.isDither = true
            this.style = Paint.Style.STROKE
            this.strokeJoin = Paint.Join.ROUND
            this.strokeCap = Paint.Cap.ROUND
            this.color = color
            this.alpha = (ALPHA_OPAQUE * MASK_PAINT_ALPHA).toInt()
            this.strokeWidth = strokeWidth
        }

        fun newDefaultPaintEraser(strokeWidth: Float) = Paint().apply {
            this.xfermode = ERASER_XFER_MODE
            this.isAntiAlias = true
            this.isDither = true
            this.style = Paint.Style.STROKE
            this.strokeJoin = Paint.Join.ROUND
            this.strokeCap = Paint.Cap.ROUND
            this.strokeWidth = strokeWidth
        }
    }

    //fields lazily initialized
    private var _width: Int? = null
    private var _height: Int? = null
    var currentScale = 1f
        set(value) {
            field = value
            paintBrushPendingRecreation = true
            paintEraserPendingRecreation = true
        }

    //fields depending on init of dimensions
    private val width by lazy { _width!! }
    private val height by lazy { _height!! }
    private val latestChangeBitmap by lazy {
        Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
    }
    private val latestChangeBitmapCanvas by lazy { Canvas(latestChangeBitmap) }
    private val currentStateBitmap by lazy {
        Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )
    }
    private val currentStateBitmapCanvas by lazy { Canvas(currentStateBitmap) }

    //fields depending on init of stage
    private lateinit var currentDiseaseStage: BrushDiseaseStage
    private lateinit var currentPaintBrush: Paint
    private lateinit var currentPaintEraser: Paint
    private lateinit var currentPaint: Paint

    //init independent fields
    private var paintBrushPendingRecreation = true
    private var paintEraserPendingRecreation = true
    private var pathsAndPaints: MutableList<PathAndPaint> = LinkedList()
    private var undoPendingPaths = 0
    private var currentPath: PointToPointPath? = null
    private var currentMode: MaskCustomView.Mode = MaskCustomView.Mode.Draw
        set(value) {
            require(arrayOf(MaskCustomView.Mode.Draw, MaskCustomView.Mode.Erase).contains(value))
            field = value
        }

    fun initDimensions(width: Int, height: Int) {
        require(_width == null && _height == null) { "Dimensions were initialized already!" }
        _width = width
        _height = height
    }

    fun initDiseaseStage(diseaseStage: BrushDiseaseStage) {
        require(!diseaseStageInitialized()) { "Disease stage was initialized already!" }
        currentDiseaseStage = diseaseStage
    }

    fun setDiseaseStage(diseaseStage: BrushDiseaseStage) {
        require(diseaseStageInitialized()) { "Disease stage was not initialized yet!" }
        currentDiseaseStage = diseaseStage
        paintBrushPendingRecreation = true
    }

    private fun dimensionsInitialized() = _width != null && _height != null

    private fun diseaseStageInitialized() = this::currentDiseaseStage.isInitialized

    fun draw(canvas: Canvas) {
        if (!dimensionsInitialized()) return
        composeCurrentStateBitmap()
        if (!pathBeingDrawn()) keepLatestChangeBitmap()
        canvas.withMatrix(imageMatrix) {
            this.drawBitmap(currentStateBitmap, 0f, 0f, BITMAP_TRANSFER_PAINT)
        }
    }

    private fun pathBeingDrawn() = currentPath != null

    private fun composeCurrentStateBitmap() {
        currentStateBitmap.eraseColor(Color.TRANSPARENT)
        drawPaths(currentStateBitmapCanvas)
    }

    private fun drawPaths(
        canvas: Canvas,
        removeAlpha: Boolean = false
    ) {
        for (i in 0 until pathsAndPaints.size - undoPendingPaths) {
            val (path, paint) = pathsAndPaints[i]
            val paintReviewed =
                if (removeAlpha) Paint(paint).apply { alpha = ALPHA_OPAQUE } else paint
            canvas.drawPath(path, paintReviewed)
        }
        currentPath?.let {
            canvas.drawPath(it, currentPaint)
        }
    }

    private fun calculateCurrentStrokeWidth() = PATH_STROKE_WIDTH / currentScale

    private fun refreshPaintBrush() {
        currentPaintBrush =
            newDefaultPaintBrush(currentDiseaseStage.maskColor, calculateCurrentStrokeWidth())
        paintBrushPendingRecreation = false
    }

    private fun refreshPaintEraser() {
        currentPaintEraser = newDefaultPaintEraser(calculateCurrentStrokeWidth())
        paintEraserPendingRecreation = false
    }

    private fun resolveCurrentPaint() {
        currentPaint = if (isCurrentModeDraw()) currentPaintBrush else currentPaintEraser
    }

    fun drawMode() {
        currentMode = MaskCustomView.Mode.Draw
    }

    fun eraseMode() {
        currentMode = MaskCustomView.Mode.Erase
    }

    fun undo() = undoPendingPaths++

    fun redo() = undoPendingPaths--

    fun undoAvailable() = (pathsAndPaints.size - undoPendingPaths) > 0

    fun redoAvailable() = undoPendingPaths > 0

    fun getBitmap(): Bitmap {
        return Bitmap.createBitmap(
            currentStateBitmap.width,
            currentStateBitmap.height,
            Bitmap.Config.ARGB_8888
        ).apply {
            val canvas = Canvas(this)
            drawPaths(canvas, removeAlpha = true)
        }
    }

    fun drawStart(x: Float, y: Float) {
        refreshPaints()
        currentPath = PointToPointPath(x, y)
    }

    private fun refreshPaints() {
        if (paintBrushPendingRecreation) refreshPaintBrush()
        if (paintEraserPendingRecreation) refreshPaintEraser()
        resolveCurrentPaint()
    }

    fun drawMove(x: Float, y: Float) {
        val (latestX, latestY) = currentPath!!.latestPoint
        val dX = abs(x - latestX)
        val dY = abs(y - latestY)
        val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop / currentScale
        if (dX >= touchTolerance || dY >= touchTolerance) {
            currentPath!!.quadTo(x, y)
        }
    }

    fun drawEnd() {
        val visibleChange: Boolean =
            if (isCurrentModeDraw()) currentPath!!.hasMultiplePoints()
            else !latestChangeBitmap.sameAs(currentStateBitmap)
        if (visibleChange) {
            keepLatestChangeBitmap()
            flushPendingUndos()
            pathsAndPaints.add(PathAndPaint(currentPath!!, currentPaint))
        }
        currentPath = null
    }

    private fun isCurrentModeDraw() = currentMode == MaskCustomView.Mode.Draw

    private fun keepLatestChangeBitmap() {
        latestChangeBitmap.eraseColor(Color.TRANSPARENT)
        latestChangeBitmapCanvas.drawBitmap(currentStateBitmap, 0f, 0f, BITMAP_TRANSFER_PAINT)
    }

    fun getInstanceState() =
        MaskCustomViewBaseState(pathsAndPaints, undoPendingPaths, currentDiseaseStage)

    fun restoreInstanceState(savedState: Parcelable?) {
        if (savedState is MaskCustomViewBaseState) {
            undoPendingPaths = savedState.undoPendingPaths
            pathsAndPaints.addAll(savedState.reassemblePathsPaintsAndStagesNames())
            currentDiseaseStage = savedState.currentBrushDiseaseStage
        }
    }

    private fun flushPendingUndos() {
        for (i in pathsAndPaints.size - 1 downTo pathsAndPaints.size - undoPendingPaths) {
            pathsAndPaints.removeAt(i)
        }
        undoPendingPaths = 0
    }

    data class PathAndPaint(
        val path: PointToPointPath,
        val paint: Paint
    )
}
