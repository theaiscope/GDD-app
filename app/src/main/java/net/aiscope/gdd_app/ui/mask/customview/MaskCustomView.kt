package net.aiscope.gdd_app.ui.mask.customview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat

class MaskCustomView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    var scaleMatrix = Matrix()

    var originalBitmapRect = RectF(0f, 0f,0f,0f)
    var originalBitmap: Bitmap? = null
    set(bmp) {
        field = bmp
        originalBitmapRect = RectF(0f, 0f, bmp?.width?.toFloat() ?: 0f, bmp?.height?.toFloat() ?: 0f)
        scaleMatrix.setRectToRect(originalBitmapRect, canvasRect, Matrix.ScaleToFit.START)

        invalidate()
    }

    val bgColor = ResourcesCompat.getColor(getResources(), android.R.color.holo_blue_light, null);
    val backgroundPaint = Paint().apply {
        color = bgColor
    }

    var canvasRect = RectF(0f,0f,0f,0f)

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        canvasRect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        scaleMatrix.setRectToRect(originalBitmapRect, canvasRect, Matrix.ScaleToFit.CENTER)
    }


    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            // draw background first
            it.drawRect(canvasRect, backgroundPaint)
            val canvas = it

            originalBitmap?.let {
                canvas.drawBitmap(it, scaleMatrix, null)
            }
        }
    }
}