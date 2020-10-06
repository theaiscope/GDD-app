package net.aiscope.gdd_app.ui.mask.customview

import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertTrue
import org.junit.Test


class MaskCustomViewTest{
    @Test
    fun touchBeforeInit(){
        Looper.prepare()
        val view = MaskCustomView(InstrumentationRegistry.getInstrumentation().context)
        val event = getEvent(view)

        assertTrue(view.onTouchEvent(event))

    }

    private fun getEvent(view: MaskCustomView): MotionEvent {
        // get the coordinates of the view
        val coordinates = IntArray(2)
        view.getLocationOnScreen(coordinates)

        // MotionEvent parameters
        val downTime = SystemClock.uptimeMillis()
        val eventTime = SystemClock.uptimeMillis()
        val action = MotionEvent.ACTION_DOWN
        val x = coordinates[0]
        val y = coordinates[1]
        val metaState = 0

        // return the event
        return MotionEvent.obtain(downTime, eventTime, action, x.toFloat(), y.toFloat(), metaState)
    }
}