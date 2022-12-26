package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.os.Parcelable
import android.util.Size
import androidx.core.graphics.component1
import androidx.core.graphics.component2
import androidx.core.graphics.withMatrix
import timber.log.Timber

@Suppress("TooManyFunctions")
class MaskLayer(private val imageMatrix: Matrix) {

    companion object {
        private const val PATH_STROKE_WIDTH = 80f
        private val BITMAP_TRANSFER_PAINT = Paint()
        private val EMPTY_MATRIX = Matrix()
    }

    enum class Mode {
        Draw,
        Erase
    }

    //fields lazily initialized
    private lateinit var size: Size
    var currentScale = 1f
        set(value) {
            field = value
            paintBrushPendingRecreation = true
            paintEraserPendingRecreation = true
        }

    private val latestChangeBitmap by lazy {
        Bitmap.createBitmap(
            size.width,
            size.height,
            Bitmap.Config.ARGB_8888
        )
    }
    private val latestChangeBitmapCanvas by lazy { Canvas(latestChangeBitmap) }

    private var initialBitmap: Bitmap? = null

    private lateinit var currentStateBitmap: Bitmap
    private lateinit var currentStateBitmapCanvas :Canvas

    //fields depending on init of stage
    private var currentBrushColor: Int = 0
    private lateinit var currentPaintBrush: Paint
    private lateinit var currentPaintEraser: Paint
    private lateinit var currentPaint: Paint

    //init independent fields
    private var paintBrushPendingRecreation = true
    private var paintEraserPendingRecreation = true
    private val pathsAndPaints = mutableListOf<PathAndPaint>()
    private var undoPendingPaths = 0
    private var currentPath: PointToPointPath? = null
    private var currentMode: Mode = Mode.Draw

    fun initSize(size: Size) {
        require(!sizeInitialized()) { "Size was initialized already!" }
        this.size = size

        currentStateBitmap = Bitmap.createBitmap(
            size.width,
            size.height,
            Bitmap.Config.ARGB_8888
        )

        Timber.tag("MaskLayer").d("Created mask bitmap with %s x %s of %s bytes",
            currentStateBitmap.height,
            currentStateBitmap.width,
            currentStateBitmap.byteCount)

        currentStateBitmapCanvas = Canvas(currentStateBitmap)
    }

    fun initBrushColor(color: Int) {
        require(!brushColorInitialized()) { "Brush color was initialized already!" }
        currentBrushColor = color
    }

    fun setBrushColor(color: Int) {
        require(brushColorInitialized()) { "Brush color was not initialized yet!" }
        currentBrushColor = color
        paintBrushPendingRecreation = true
    }

    private fun sizeInitialized() = this::size.isInitialized

    private fun brushColorInitialized() = currentBrushColor != 0
    fun draw(canvas: Canvas) {
        if (!sizeInitialized()) return

        composeCurrentStateBitmap()
        if (!pathBeingDrawn()) {
            keepLatestChangeBitmap()
        }
        canvas.withMatrix(imageMatrix) {
            this.drawBitmap(currentStateBitmap, 0f, 0f, BITMAP_TRANSFER_PAINT)
        }
    }

    fun setMaskBitmap(bitmap: Bitmap){
        require(sizeInitialized()) { "Size was not initialized yet" }
        initialBitmap = bitmap
        currentStateBitmapCanvas.drawBitmap(bitmap, EMPTY_MATRIX, null)
    }

    private fun pathBeingDrawn() = currentPath != null

    private fun composeCurrentStateBitmap() {
        currentStateBitmap.eraseColor(Color.TRANSPARENT)

        initialBitmap?.let{
            currentStateBitmapCanvas.drawBitmap(it, EMPTY_MATRIX, null)
        }

        drawPaths(currentStateBitmapCanvas)
    }

    private fun drawPaths(
        canvas: Canvas,
        removeAlpha: Boolean = false
    ) {
        for (i in 0 until pathsAndPaints.size - undoPendingPaths) {
            val (path, paint) = pathsAndPaints[i]
            val paintReviewed =
                if (removeAlpha) Paint(paint).apply { alpha = PaintFactory.ALPHA_OPAQUE } else paint
            path.draw(canvas, paintReviewed)
        }
        currentPath?.draw(canvas, currentPaint)
    }

    private fun PointToPointPath.draw(canvas: Canvas, paint: Paint) {
        if (!hasMultiplePoints()) {
            val (x, y) = firstPoint()
            canvas.drawPoint(x, y, paint)
        } else {
            canvas.drawPath(this, paint)
        }
    }

    private fun calculateCurrentStrokeWidth() = PATH_STROKE_WIDTH / currentScale

    private fun refreshPaintBrush() {
        currentPaintBrush =
            PaintFactory.newDefaultPaintBrush(currentBrushColor, calculateCurrentStrokeWidth())
        paintBrushPendingRecreation = false
    }

    private fun refreshPaintEraser() {
        currentPaintEraser = PaintFactory.newDefaultPaintEraser(calculateCurrentStrokeWidth())
        paintEraserPendingRecreation = false
    }

    private fun resolveCurrentPaint() {
        currentPaint = if (isCurrentModeDraw()) currentPaintBrush else currentPaintEraser
    }

    fun drawMode() {
        currentMode = Mode.Draw
    }

    fun eraseMode() {
        currentMode = Mode.Erase
    }

    fun undo() = undoPendingPaths++

    fun redo() = undoPendingPaths--

    fun undoAvailable() = (pathsAndPaints.size - undoPendingPaths) > 0

    fun redoAvailable() = undoPendingPaths > 0

    fun getBitmap(): Bitmap {
        val backgroundBitmap = initialBitmap ?: Bitmap.createBitmap(
            currentStateBitmap.width,
            currentStateBitmap.height,
            Bitmap.Config.ARGB_8888)

        return backgroundBitmap.apply {
            val canvas = Canvas(this)
            drawPaths(canvas, removeAlpha = true)
        }
    }

    fun drawStart(x: Float, y: Float) {
        refreshPaints()
        currentPath = PointToPointPath.createWithStartingPoint(PointF(x, y))
    }

    private fun refreshPaints() {
        if (paintBrushPendingRecreation) refreshPaintBrush()
        if (paintEraserPendingRecreation) refreshPaintEraser()
        resolveCurrentPaint()
    }

    fun drawMove(x: Float, y: Float) {
        currentPath?.quadTo(x, y)
    }

    fun drawEnd() {
        currentPath?.run {
            if (!latestChangeBitmap.sameAs(currentStateBitmap)) {
                keepLatestChangeBitmap()
                flushPendingUndos()
                pathsAndPaints.add(PathAndPaint(this, currentPaint))
            }
            currentPath = null
        }
    }

    private fun isCurrentModeDraw() = currentMode == Mode.Draw

    private fun keepLatestChangeBitmap() {
        latestChangeBitmap.eraseColor(Color.TRANSPARENT)
        latestChangeBitmapCanvas.drawBitmap(currentStateBitmap, 0f, 0f, BITMAP_TRANSFER_PAINT)
    }

    fun getInstanceState() =
        PhotoMaskViewBaseState(pathsAndPaints, undoPendingPaths, currentBrushColor)

    fun restoreInstanceState(savedState: Parcelable?) {
        if (savedState is PhotoMaskViewBaseState) {
            undoPendingPaths = savedState.undoPendingPaths
            pathsAndPaints.addAll(savedState.pathsPaintsAndStagesNames)
            currentBrushColor = savedState.currentBrushColor
        }
    }

    private fun flushPendingUndos() {
        for (i in pathsAndPaints.size - 1 downTo pathsAndPaints.size - undoPendingPaths) {
            pathsAndPaints.removeAt(i)
        }
        undoPendingPaths = 0
    }
}
