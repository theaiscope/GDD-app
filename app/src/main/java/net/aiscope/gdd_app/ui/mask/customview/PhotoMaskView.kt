package net.aiscope.gdd_app.ui.mask.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Size
import android.view.MotionEvent
import androidx.core.util.component1
import androidx.core.util.component2
import com.github.chrisbanes.photoview.PhotoView
import net.aiscope.gdd_app.extensions.x

@Suppress("TooManyFunctions")
class PhotoMaskView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_SCALE = 10f
        private const val MATRIX_SIZE = 9
    }

    enum class Mode {
        Zoom,
        Draw,
        Erase
    }

    var onMaskingActionFinishedListener: OnTouchListener? = null
    private var readyForEvents = false
    private val maskLayer = MaskLayer(imageMatrix)
    private var currentMode: Mode = Mode.Draw
    private lateinit var drawableSize: Size

    init {
        maximumScale = MAX_SCALE

        setOnMatrixChangeListener {
            val imageMatrixArray = FloatArray(MATRIX_SIZE).apply { imageMatrix.getValues(this) }
            val scale = imageMatrixArray[Matrix.MSCALE_X]
            maskLayer.currentScale = scale
        }

        setOnTouchListener { _, event -> onTouchEvent(event) }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        readyForEvents && (
                super.onTouchEvent(event) ||
                        when (currentMode) {
                            Mode.Zoom -> onTouchMove(event)
                            Mode.Draw, Mode.Erase -> onTouchDraw(event)
                        }
                )

    private fun onTouchMove(event: MotionEvent) = attacher.onTouch(this, event)

    private fun onTouchDraw(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            maskLayer.drawEnd()
            onMaskingActionFinishedListener?.onTouch(this, event)
            invalidate()
        } else {
            val (x, y) = invert(event.x, event.y)
            val (drawableWidth, drawableHeight) = drawableSize
            val xOffBounds = 0 > x || x > drawableWidth
            val yOffBounds = 0 > y || y > drawableHeight
            if (xOffBounds || yOffBounds) return false
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    maskLayer.drawStart(x, y)
                    invalidate()
                }
                MotionEvent.ACTION_MOVE -> {
                    maskLayer.drawMove(x, y)
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
        maskLayer.draw(canvas)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        drawableSize = (drawable?.intrinsicWidth ?: 0) x (drawable?.intrinsicHeight ?: 0)
        maskLayer.initSize(drawableSize)
        readyForEvents = true
    }

    override fun onSaveInstanceState(): Parcelable =
        PhotoMaskViewSavedState(
            super.onSaveInstanceState(),
            maskLayer.getInstanceState()
        )

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(state)
        if (state is PhotoMaskViewSavedState) {
            maskLayer.restoreInstanceState(state.maskLayerState)
        }
    }

    fun zoomMode() {
        currentMode = Mode.Zoom
    }

    fun drawMode() {
        maskLayer.drawMode()
        currentMode = Mode.Draw
    }

    fun eraseMode() {
        maskLayer.eraseMode()
        currentMode = Mode.Erase
    }

    fun initBrushColor(color: Int) =
        maskLayer.initBrushColor(color)

    fun setBrushColor(color: Int) =
        maskLayer.setBrushColor(color)

    fun getMaskBitmap() = maskLayer.getBitmap()

    fun setMaskBitmap(bitmap: Bitmap) {
        maskLayer.setMaskBitmap(bitmap)
        invalidate()
    }

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

    fun stopDrawing() {
        readyForEvents = false
    }

    class PhotoMaskViewSavedState(superState: Parcelable?, val maskLayerState: Parcelable) :
        BaseSavedState(superState)
}
