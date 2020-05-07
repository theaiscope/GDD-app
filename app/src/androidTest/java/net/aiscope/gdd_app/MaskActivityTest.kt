package net.aiscope.gdd_app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.action.ViewActions.swipeUp
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.screenshot.Screenshot
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import kotlinx.android.synthetic.main.activity_mask.*
import net.aiscope.gdd_app.ui.mask.MaskActivity
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream

//FIXME("Some tests will fail depending on the device's screen aspect ratio - swipes won't start/end
// in the picture, so their expected results will not happen")
@RunWith(AndroidJUnit4::class)
class MaskActivityTest {

    private val activityTestRule = ActivityTestRule(MaskActivity::class.java, true, false)

    private fun startActivity() {
        val tempFile = File.createTempFile("img", ".png")
        val outputStream = tempFile.outputStream()
        val applicationContext = getInstrumentation().targetContext.applicationContext
        getAssetStream(applicationContext, "photo.png").copyTo(outputStream)

        activityTestRule.launchActivity(
            Intent(Intent.ACTION_MAIN)
                .putExtra(MaskActivity.EXTRA_DISEASE_NAME, applicationContext.resources.getString(R.string.malaria_name))
                .putExtra(MaskActivity.EXTRA_IMAGE_NAME, tempFile.absolutePath)
                .putExtra(MaskActivity.EXTRA_MASK_NAME, "mask")
        )
    }

    private fun getAssetStream(context: Context, fileName: String): InputStream {
        return try {
            context.resources.assets.open(fileName)
        } catch (ex: FileNotFoundException) {
            javaClass.classLoader!!.getResourceAsStream("assets" + File.separator + fileName)
        }
    }

    private fun rotateAndWaitViewDisplay(orientation: Orientation, viewId: Int) {
        rotate(orientation) {activityTestRule.activity}
        checkIsDisplayed(viewId)
    }

    enum class Orientation(val requestedOrientation: Int, val configurationOrientation: Int) {
        PORTRAIT(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT, Configuration.ORIENTATION_PORTRAIT),
        LANDSCAPE(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, Configuration.ORIENTATION_LANDSCAPE)
    }

    private fun rotate(orientation: Orientation, activity: () -> Activity) {
        activity().requestedOrientation = orientation.requestedOrientation
        ConditionWatcher.waitForCondition(object : Instruction() {
            override fun getDescription(): String {
                return "Orientation should be ${orientation.name}"
            }

            override fun checkCondition(): Boolean {
                return activity().resources.configuration.orientation == orientation.configurationOrientation
            }
        })
    }

    private fun checkIsDisplayed(viewId: Int) {
        onView(withId(viewId)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun checkIsVisible(viewId: Int) {
        checkVisibility(viewId, ViewMatchers.Visibility.VISIBLE)
    }

    private fun checkIsInvisible(viewId: Int) {
        checkVisibility(viewId, ViewMatchers.Visibility.INVISIBLE)
    }

    private fun checkVisibility(viewId: Int, visibility: ViewMatchers.Visibility) {
        onView(withId(viewId)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(visibility)))
    }

    private fun perform(viewId: Int, action: ViewAction): ViewInteraction {
        return onView(withId(viewId)).perform(action)
    }

    @Test
    fun shouldKeepMaskOnRotation() {
        startActivity()

        perform(R.id.mask_custom_view, swipeLeft())

        val captureBeforeRotation = captureMaskCustomView()

        rotateAndWaitViewDisplay(Orientation.LANDSCAPE, R.id.mask_custom_view)

        rotateAndWaitViewDisplay(Orientation.PORTRAIT, R.id.mask_custom_view)

        val captureAfterRotation = captureMaskCustomView()

        assertTrue(captureAfterRotation.bitmap.sameAs(captureBeforeRotation.bitmap))
    }

    @Test
    fun shouldKeepUndoAndRedoOnRotation() {
        startActivity()

        perform(R.id.mask_custom_view, swipeLeft())
        perform(R.id.mask_custom_view, swipeUp())

        perform(R.id.undo_btn, click())

        rotateAndWaitViewDisplay(Orientation.LANDSCAPE, R.id.mask_custom_view)

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        rotateAndWaitViewDisplay(Orientation.PORTRAIT, R.id.mask_custom_view)

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)
    }

    private fun captureMaskCustomView() = Screenshot.capture(activityTestRule.activity.mask_custom_view)

    @Test
    fun shouldUndoAndRedoProperly() {
        startActivity()

        checkIsInvisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureStart = captureMaskCustomView()

        perform(R.id.mask_custom_view, swipeLeft())

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureFirstPath = captureMaskCustomView()

        perform(R.id.mask_custom_view, swipeUp())

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureSecondPath = captureMaskCustomView()

        perform(R.id.undo_btn, click())

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        assertTrue(captureMaskCustomView().bitmap.sameAs(captureFirstPath.bitmap))

        perform(R.id.undo_btn, click())

        checkIsInvisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        assertTrue(captureMaskCustomView().bitmap.sameAs(captureStart.bitmap))

        perform(R.id.redo_btn, click())

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        assertTrue(captureMaskCustomView().bitmap.sameAs(captureFirstPath.bitmap))

        perform(R.id.redo_btn, click())

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        assertTrue(captureMaskCustomView().bitmap.sameAs(captureSecondPath.bitmap))
    }


    @Test
    fun shouldUndoAndRedoProperlyWithRotation() {
        startActivity()

        checkIsInvisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureStart = captureMaskCustomView()

        perform(R.id.mask_custom_view, swipeLeft())

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureFirstPath = captureMaskCustomView()

        rotateAndWaitViewDisplay(Orientation.LANDSCAPE, R.id.mask_custom_view)
        perform(R.id.mask_custom_view, swipeUp())
        rotateAndWaitViewDisplay(Orientation.PORTRAIT, R.id.mask_custom_view)

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureSecondPath = captureMaskCustomView()

        rotateAndWaitViewDisplay(Orientation.LANDSCAPE, R.id.mask_custom_view)
        perform(R.id.undo_btn, click())
        rotateAndWaitViewDisplay(Orientation.PORTRAIT, R.id.mask_custom_view)

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

         assertTrue(captureMaskCustomView().bitmap.sameAs(captureFirstPath.bitmap))

        perform(R.id.undo_btn, click())

        checkIsInvisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        assertTrue(captureMaskCustomView().bitmap.sameAs(captureStart.bitmap))

        rotateAndWaitViewDisplay(Orientation.LANDSCAPE, R.id.mask_custom_view)
        perform(R.id.redo_btn, click())
        rotateAndWaitViewDisplay(Orientation.PORTRAIT, R.id.mask_custom_view)

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        assertTrue(captureMaskCustomView().bitmap.sameAs(captureFirstPath.bitmap))

        perform(R.id.redo_btn, click())

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        assertTrue(captureMaskCustomView().bitmap.sameAs(captureSecondPath.bitmap))
    }
}
