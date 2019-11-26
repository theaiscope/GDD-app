package net.aiscope.gdd_app.ui.mask.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import net.aiscope.gdd_app.R

const val TWENTY = 20.0f

class MaskLayer(
    private val context: Context,
    private val scaleMatrix: Matrix
) {

    private var viewHeight: Int = 0
    private var viewWidth: Int = 0
    private var maskBitmap: Bitmap? = null
    private var maskCanvas: Canvas? = null
    private val maskPaintBitmap = Paint().apply {
        alpha = MASK_PAINT_ALPHA
    }
    private val maskPaintBrush = Paint().apply {
        color = context.getColor(R.color.colorPrimary)
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private val maskPaintErase = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
        isDither = true
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private var maskPaint = maskPaintBrush

    fun init(width: Int, height: Int) {
        if (width > 0 && height > 0) {
            if (maskBitmap?.width != width && maskBitmap?.height != height) {
                maskBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    .also { maskCanvas = Canvas(it) }
            }
        } else {
            maskCanvas = null
        }
        recreateBrush()
    }

    fun draw(canvas: Canvas) {
        maskBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, maskPaintBitmap)
        }
    }

    fun onViewResize(width: Int, height: Int) {
        this.viewWidth = width
        this.viewHeight = height
        recreateBrush()
    }

    fun onScaleChanged() {
        recreateBrush()
    }

    fun onPath(path: Path) {
        maskCanvas?.drawPath(path, maskPaint)
    }

    private fun recreateBrush() {
        val scale = getScaleFromMatrix(this.scaleMatrix)

        maskPaintBrush.apply {
            strokeWidth = viewHeight.coerceAtLeast(viewWidth).toFloat() / (TWENTY * scale)
        }

        maskPaintErase.apply {
            strokeWidth = viewHeight.coerceAtLeast(viewWidth).toFloat() / (TWENTY * scale)
        }
    }

    fun enterDrawMode() {
        maskPaint = maskPaintBrush
    }

    fun enterEraseMode() {
        maskPaint = maskPaintErase
    }

    private fun getScaleFromMatrix(m: Matrix): Float {
        val p = FloatArray(MATRIX_SIZE)
        m.getValues(p)

        return p[Matrix.MSCALE_X]
    }

    fun getBitmap(): Bitmap? = maskBitmap

    companion object {
        private const val MATRIX_SIZE = 9
        private const val MASK_PAINT_ALPHA = 200
    }
}
