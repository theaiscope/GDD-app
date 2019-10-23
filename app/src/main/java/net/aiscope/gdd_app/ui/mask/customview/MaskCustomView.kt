@file:Suppress("ForbiddenComment")

package net.aiscope.gdd_app.ui.mask.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import net.aiscope.gdd_app.R
import kotlin.math.abs


const val TWENTY = 20.0f

class MaskCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    enum class DrawMode {
        Brush,
        Erase,
        Move
    }
    // Transformation for canvas to view
    var scaleMatrix = Matrix()

    // transformation from touches to canvas
    var inverseScaleMatrix = Matrix()
    private val path = Path()

    private var originalBitmapRect = RectF(0f, 0f,0f,0f)
    var originalBitmap: Bitmap? = null
    set(bmp) {
        field = bmp
        originalBitmapRect = RectF(0f, 0f, bmp?.width?.toFloat() ?: 0f, bmp?.height?.toFloat() ?: 0f)
        if (bmp != null) {
            maskBitmap = Bitmap.createBitmap(bmp.width, bmp.height, Bitmap.Config.ARGB_8888)
            maskCanvas = Canvas(maskBitmap)
        }
        createMatrix()
        recreateBrush()

        invalidate()
    }

    private var maskCanvas: Canvas? = null
    var maskBitmap: Bitmap? = null
    private var maskPaintBrush = Paint()
    private var maskPaintErase = Paint()
    private var maskPaint = maskPaintBrush

    private val backgroundPaint = Paint().apply {
        color = BACKGROUND_PAINT_COLOR
    }

    private val maskPaintBitmap = Paint().apply {
        alpha = MASK_PAINT_ALPHA
    }

    private var canvasRect = RectF(0f,0f,0f,0f)

    var mode: DrawMode = DrawMode.Brush
        set(value) {
            field = value
            maskPaint = when (mode) {
                DrawMode.Brush -> maskPaintBrush
                DrawMode.Erase -> maskPaintErase
                else -> maskPaintBrush
            }
        }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        canvasRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        createMatrix()
        recreateBrush()
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            // draw background first
            it.drawRect(canvasRect, backgroundPaint)
            val canvas = it

            originalBitmap?.let {
                canvas.drawBitmap(it, scaleMatrix, null)
            }

            maskBitmap?.let {
                canvas.drawBitmap(it, scaleMatrix, maskPaintBitmap)
            }
        }
    }

    private var mX = 0f
    private var mY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {

        mScaleDetector.onTouchEvent(event)

        if(event.pointerCount > 1) {
            return true
        }

        val (x, y) = transformPoint(event.x, event.y)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart(x, y)
            MotionEvent.ACTION_MOVE -> {
                touchMove(x, y)
                invalidate()
            }
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun touchStart(x: Float, y: Float) {
        path.moveTo(x, y)
        mX = x
        mY = y
    }

    private fun touchMove(x: Float, y: Float) {
        val dx = abs(x - mX)
        val dy = abs(y - mY)
        if (mode == DrawMode.Move) {
            scaleMatrix.preTranslate(x- mX, y - mY)
            scaleMatrix.invert(inverseScaleMatrix)
            return
        }
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)

            mX = x
            mY = y

            // TODO: improve performance, see if we can to canvas on touch-up
            maskCanvas?.drawPath(path, maskPaint)
        }
    }

    private fun touchUp() {
        path.reset()
        invalidate()
    }


    private val scaleListener = object : ScaleGestureDetector.OnScaleGestureListener {
        private var lastSpanX: Float = 0f
        private var lastSpanY: Float = 0f
        var scale: Float = 0f

        override fun onScaleBegin(scaleGestureDetector: ScaleGestureDetector): Boolean {
            lastSpanX = scaleGestureDetector.currentSpanX
            lastSpanY = scaleGestureDetector.currentSpanY


            val floats = FloatArray(MATRIX_SIZE)
            scaleMatrix.getValues(floats)
            scale = floats[Matrix.MSCALE_X]

            return true
        }

        override fun onScaleEnd(p0: ScaleGestureDetector?) {
            scaleMatrix.setScale(scale, scale)
            scaleMatrix.invert(inverseScaleMatrix)

            invalidate()
        }

        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            scale *= scaleGestureDetector.scaleFactor

            scaleMatrix.setScale(scale, scale)
            scaleMatrix.invert(inverseScaleMatrix)

            recreateBrush()

            invalidate()
            return true
        }
    }

    private val mScaleDetector = ScaleGestureDetector(context, scaleListener)

    private fun transformPoint(x: Float, y: Float): Pair<Float, Float> {
        val p = FloatArray(2) {
            when (it) {
                0 -> x
                1 -> y
                else -> -1f
            }
        }
        inverseScaleMatrix.mapPoints(p)

        return Pair(p[0], p[1])
    }

    private fun getScaleFromMatrix(m: Matrix): Float {
        val p = FloatArray(MATRIX_SIZE)
        m.getValues(p)

        return p[Matrix.MSCALE_X]
    }

    private fun createMatrix() {
        // Get the matrix to show the bitmap scaled and centered in the view
        scaleMatrix.setRectToRect(originalBitmapRect, canvasRect, Matrix.ScaleToFit.CENTER)
        scaleMatrix.invert(inverseScaleMatrix)
    }

    private fun recreateBrush() {
        val viewWidth = this.width
        val viewHeight = this.height
        val scale = getScaleFromMatrix(this.scaleMatrix)

        maskPaintBrush = Paint().apply {

            color = context.getColor(R.color.colorPrimary)
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = viewHeight.coerceAtLeast(viewWidth).toFloat() / (TWENTY * scale)
        }

        maskPaintErase =  Paint().apply {
            xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            isAntiAlias = true
            isDither = true
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.ROUND
            strokeCap = Paint.Cap.ROUND
            strokeWidth = viewHeight.coerceAtLeast(viewWidth).toFloat() / (TWENTY * scale)
        }

        if (mode == DrawMode.Erase) {
            maskPaint = maskPaintErase
        }
        if (mode == DrawMode.Brush) {
            maskPaint = maskPaintBrush
        }
    }

    companion object {
        private const val TOUCH_TOLERANCE = 4f
        private const val MATRIX_SIZE = 9
        private const val BACKGROUND_PAINT_COLOR = 0xFFFFFFFF.toInt()
        private const val MASK_PAINT_ALPHA = 200
    }
}
