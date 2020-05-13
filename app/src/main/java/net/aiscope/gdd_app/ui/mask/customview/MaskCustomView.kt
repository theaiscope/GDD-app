package net.aiscope.gdd_app.ui.mask.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import com.github.chrisbanes.photoview.PhotoView
import net.aiscope.gdd_app.ui.mask.BrushDiseaseStage

@Suppress("TooManyFunctions")
//TODO("Rename to PhotoMaskView")
class MaskCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_SCALE = 5f
        private const val MATRIX_SIZE = 9
    }

    enum class Mode {
        Zoom,
        Draw,
        Erase
    }

    var onTouchActionUpListener: OnTouchListener? = null
    private val maskLayerController = MaskLayer(context, imageMatrix)
    private var currentMode: Mode = Mode.Draw
    private lateinit var drawableDimensions: Pair<Int, Int>

    init {
        maximumScale = MAX_SCALE

        setOnMatrixChangeListener { _ ->
            val imageMatrixArray = FloatArray(MATRIX_SIZE).apply { imageMatrix.getValues(this) }
            val scale = imageMatrixArray[Matrix.MSCALE_X]
            maskLayerController.currentScale = scale
        }

        setOnTouchListener { _, event ->  onTouchEvent(event) }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        super.onTouchEvent(event) ||
                when (currentMode) {
                    Mode.Zoom -> onTouchMove(event)
                    Mode.Draw, Mode.Erase -> onTouchDraw(event)
                }

    private fun onTouchMove(event: MotionEvent) = attacher.onTouch(this, event)

    private fun onTouchDraw(event: MotionEvent): Boolean {
        val (x, y) = invert(event.x, event.y)
        val (drawableWidth, drawableHeight) = drawableDimensions
        if (event.action == MotionEvent.ACTION_UP) {
            maskLayerController.drawEnd()
            onTouchActionUpListener?.onTouch(this, event)
            invalidate()
        } else {
            val xOffBounds = 0 > x || x > drawableWidth
            val yOffBounds = 0 > y || y > drawableHeight
            if (xOffBounds || yOffBounds) return false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> maskLayerController.drawStart(x, y)
                MotionEvent.ACTION_MOVE -> {
                    maskLayerController.drawMove(x, y)
                    invalidate()
                }
            }
        }
        return true
    }

    private fun invert(x: Float, y: Float): Pair<Float, Float> {
        val invertedScaleMatrix = Matrix().apply { imageMatrix.invert(this) }
        val p = floatArrayOf(x, y).apply { invertedScaleMatrix.mapPoints(this) }
        return p[0] to p[1]
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        maskLayerController.draw(canvas)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        val (width, height) = (drawable?.intrinsicWidth ?: 0) to (drawable?.intrinsicHeight ?: 0)
        maskLayerController.initDimensions(width, height)
        drawableDimensions = width to height
    }

    override fun onSaveInstanceState(): Parcelable? =
        MaskCustomViewSavedState(
            super.onSaveInstanceState(),
            maskLayerController.getInstanceState()
        )

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state is MaskCustomViewSavedState) {
            maskLayerController.restoreInstanceState(state.maskLayerControllerState)
        }
    }

    fun zoomMode() {
        currentMode = Mode.Zoom
    }

    fun drawMode() {
        maskLayerController.drawMode()
        currentMode = Mode.Draw
    }

    fun eraseMode() {
        maskLayerController.eraseMode()
        currentMode = Mode.Erase
    }

    fun initBrushDiseaseStage(brushDiseaseStage: BrushDiseaseStage) =
        maskLayerController.initDiseaseStage(brushDiseaseStage)

    fun setBrushDiseaseStage(brushDiseaseStage: BrushDiseaseStage) =
        maskLayerController.setDiseaseStage(brushDiseaseStage)

    fun getMaskBitmap() = maskLayerController.getBitmap()

    fun undo() {
        maskLayerController.undo()
        invalidate()
    }

    fun redo() {
        maskLayerController.redo()
        invalidate()
    }

    fun undoAvailable() = maskLayerController.undoAvailable()

    fun redoAvailable() = maskLayerController.redoAvailable()

    class MaskCustomViewSavedState(
        superState: Parcelable?,
        val maskLayerControllerState: Parcelable
    ) :
        BaseSavedState(superState)
}
