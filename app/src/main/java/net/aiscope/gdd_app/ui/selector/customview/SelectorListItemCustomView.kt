package net.aiscope.gdd_app.ui.selector.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.ShapeDrawable
import android.util.AttributeSet
import net.aiscope.gdd_app.R

class SelectorListItemCustomView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    androidx.appcompat.widget.AppCompatCheckedTextView(context, attrs, defStyleAttr) {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(
        context,
        attrs,
        android.R.attr.checkedTextViewStyle
    )

    companion object {
        private fun setDrawableBounds(
            drawable: Drawable,
            view: SelectorListItemCustomView,
            density: Float
        ) {
            val limitRight = pxToDp(84, density)
            val limitBottom = view.height - view.paddingBottom
            val limitTop = view.paddingTop
            val drawableLeft = limitRight - drawable.intrinsicWidth
            val availableHeight = limitBottom - limitTop
            val drawableTop = limitTop + (availableHeight - drawable.intrinsicHeight) / 2
            val drawableBottom = drawableTop + drawable.intrinsicHeight
            drawable.setBounds(drawableLeft, drawableTop, limitRight, drawableBottom)
        }

        private fun pxToDp(px: Int, density: Float) = (px * density + 0.5f).toInt()
    }

    private var colorDrawable: Drawable = ShapeDrawable()

    fun setColor(color: Int) {
        val brushHeadless = context.resources.getDrawable(R.drawable.ic_brush_handle, null)
        val brushHead = context.resources.getDrawable(R.drawable.ic_brush_head, null).apply {
            colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
        colorDrawable = LayerDrawable(arrayOf(brushHeadless, brushHead))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setDrawableBounds(colorDrawable, this, resources.displayMetrics.density)
        colorDrawable.draw(canvas)
    }
}