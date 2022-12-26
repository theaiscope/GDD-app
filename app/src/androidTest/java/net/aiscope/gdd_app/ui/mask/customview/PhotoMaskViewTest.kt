package net.aiscope.gdd_app.ui.mask.customview

import android.graphics.drawable.Drawable
import android.os.Looper
import android.os.SystemClock
import android.view.MotionEvent
import androidx.test.platform.app.InstrumentationRegistry
import net.aiscope.gdd_app.test.extensions.getAssetStream
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.File
import java.io.InputStream

class PhotoMaskViewTest{
    private val testContext = InstrumentationRegistry.getInstrumentation().context
    private val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun init(){
        initializeLooper()
    }

    @Test
    fun touchBeforeInit(){
        val view = PhotoMaskView(testContext)
        val event = getEvent(view)

        assertFalse(view.onTouchEvent(event))
    }

    @Test
    fun touchAfterInit(){
        val view = PhotoMaskView(testContext)
        val targetFile = File(targetContext.filesDir, "test.png")
        targetFile.createNewFile()
        testContext.getAssetStream("photo.png").toFile(targetFile)
        view.setImageDrawable(Drawable.createFromPath(targetFile.absolutePath))

        val event = getEvent(view)

        assertTrue(view.onTouchEvent(event))
    }


    private fun getEvent(view: PhotoMaskView): MotionEvent {
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

    private fun InputStream.toFile(file: File) {
        file.outputStream().use { this.copyTo(it) }
    }

    private fun initializeLooper() {
        if (Looper.myLooper() == null) {
            Looper.prepare()
        }
    }

}