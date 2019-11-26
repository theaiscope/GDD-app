package net.aiscope.gdd_app.ui.mask.customview

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.graphics.withMatrix
import com.github.chrisbanes.photoview.PhotoView
import kotlin.math.abs

class MaskCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PhotoView(context, attrs, defStyleAttr) {

    private val maskLayer: MaskLayer = MaskLayer(context, imageMatrix)

    enum class DrawMode {
        Brush,
        Erase,
        Move
    }

    var mode: DrawMode = DrawMode.Brush
        set(value) {
            field = value
            when (mode) {
                DrawMode.Brush -> maskLayer.enterDrawMode()
                DrawMode.Erase -> maskLayer.enterEraseMode()
            }
        }


    init {
        setOnMatrixChangeListener {
            maskLayer.onScaleChanged()
        }

        setOnTouchListener(object : OnTouchListener {
            private val path = Path()
            private var mX = 0f
            private var mY = 0f
            private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (mode == DrawMode.Move) {
                    return attacher.onTouch(v, event)
                } else {
                    val (x, y) = transformPointInverse(event.x, event.y)

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            path.moveTo(x, y)
                            mX = x
                            mY = y
                        }
                        MotionEvent.ACTION_UP -> path.reset()
                        MotionEvent.ACTION_MOVE -> {
                            val dx = abs(x - mX)
                            val dy = abs(y - mY)
                            if (dx >= touchTolerance || dy >= touchTolerance) {
                                path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)

                                mX = x
                                mY = y

                                maskLayer.onPath(path)
                                invalidate()
                            }
                        }
                    }
                    return true
                }
            }

            private fun transformPointInverse(x: Float, y: Float): Pair<Float, Float> {
                val p = floatArrayOf(x, y)
                val inverseScaleMatrix = Matrix()
                imageMatrix.invert(inverseScaleMatrix)

                inverseScaleMatrix.mapPoints(p)

                return p[0] to p[1]
            }
        })
    }

    fun getMaskBitmap(): Bitmap? {
        return maskLayer.getBitmap()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.withMatrix(imageMatrix) {
            maskLayer.draw(this)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        maskLayer.onViewResize(width, height)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)

        val drawableWidth = drawable?.intrinsicWidth ?: 0
        val drawableHeight = drawable?.intrinsicHeight ?: 0
        maskLayer.init(drawableWidth, drawableHeight)
    }
}
