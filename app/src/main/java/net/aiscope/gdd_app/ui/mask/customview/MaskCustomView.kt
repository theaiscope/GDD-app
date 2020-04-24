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
class MaskCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {

    companion object {
        const val MAX_SCALE = 5f
    }

    enum class Mode {
        Zoom,
        Draw
    }

    private val maskLayer: MaskLayer = MaskLayer(context, imageMatrix)
    private var currentMode: Mode = Mode.Draw
    private lateinit var drawableDimensions: Pair<Int, Int>

    init {
        maximumScale = MAX_SCALE

        setOnMatrixChangeListener { maskLayer.onScaleChanged() }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        super.onTouchEvent(event) ||
                when (currentMode) {
                    Mode.Zoom -> onTouchMove(event)
                    Mode.Draw -> onTouchDraw(event)
                }

    private fun onTouchMove(event: MotionEvent) = attacher.onTouch(this, event)

    private fun onTouchDraw(event: MotionEvent): Boolean {
        val (x, y) = invert(event.x, event.y)
        val (drawableWidth, drawableHeight) = drawableDimensions
        val xOffBounds = 0 > x || x > drawableWidth
        val yOffBounds = 0 > y || y > drawableHeight
        if (xOffBounds || yOffBounds) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> maskLayer.drawStart(x, y)
            MotionEvent.ACTION_MOVE -> {
                maskLayer.drawMove(x, y)
                invalidate()
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
        maskLayer.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(width, height)
        maskLayer.onViewSizeChanged(width, height)
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        maskLayer.onViewSizeChanged(width, height)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        drawableDimensions = (drawable?.intrinsicWidth ?: 0) to (drawable?.intrinsicHeight ?: 0)
        val (drawableWidth, drawableHeight) = drawableDimensions
        maskLayer.init(drawableWidth, drawableHeight)
    }

    override fun onSaveInstanceState(): Parcelable? =
        MaskCustomViewSavedState(
            super.onSaveInstanceState(),
            maskLayer.onSaveInstanceState()
        )

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state is MaskCustomViewSavedState) {
            maskLayer.onRestoreInstanceState(state.maskLayerState)
        }
    }

    fun zoomMode() {
        currentMode = Mode.Zoom
    }

    fun drawMode() {
        currentMode = Mode.Draw
    }

    fun getBrushDiseaseStage() = maskLayer.brushDiseaseStage

    fun setBrushDiseaseStage(brushDiseaseStage: BrushDiseaseStage) {
        maskLayer.brushDiseaseStage = brushDiseaseStage
    }

    fun getMaskBitmap() = maskLayer.getBitmap()

    fun undo() {
        maskLayer.undo()
        invalidate()
    }

    fun redo() {
        maskLayer.redo()
        invalidate()
    }

    fun undoAvailable() = maskLayer.undoAvailable()

    fun redoAvailable() = maskLayer.redoAvailable()

    class MaskCustomViewSavedState(superState: Parcelable?, val maskLayerState: Parcelable) :
        BaseSavedState(superState)
}
