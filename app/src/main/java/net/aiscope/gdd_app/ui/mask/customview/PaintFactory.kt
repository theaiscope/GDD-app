package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode

internal object PaintFactory {

    const val ALPHA_OPAQUE = 0xFF
    val ERASER_XFER_MODE = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

    private const val MASK_PAINT_OPACITY = .8

    fun newDefaultPaintBrush(color: Int, strokeWidth: Float) = Paint().apply {
        this.isAntiAlias = true
        this.isDither = true
        this.style = Paint.Style.STROKE
        this.strokeJoin = Paint.Join.ROUND
        this.strokeCap = Paint.Cap.ROUND
        this.color = color
        this.alpha = (ALPHA_OPAQUE * MASK_PAINT_OPACITY).toInt()
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
