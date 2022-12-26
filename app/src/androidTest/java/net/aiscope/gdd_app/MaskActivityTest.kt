package net.aiscope.gdd_app

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.GeneralLocation
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.screenshot.Screenshot
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import net.aiscope.gdd_app.test.extensions.getAssetStream
import net.aiscope.gdd_app.ui.mask.MaskActivity
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

// FIXME("Some tests will fail depending on the device's screen aspect ratio - swipes won't start/end
// in the picture, so their expected results will not happen")
@RunWith(AndroidJUnit4::class)
class MaskActivityTest {

    private val activityTestRule = ActivityTestRule(MaskActivity::class.java, true, false)

    private fun startActivity() {
        val tempFile = File.createTempFile("img", ".png")
        val outputStream = tempFile.outputStream()
        val applicationContext = getInstrumentation().targetContext.applicationContext
        applicationContext.getAssetStream("photo.png").copyTo(outputStream)

        activityTestRule.launchActivity(
            Intent(Intent.ACTION_MAIN)
                .putExtra(
                    MaskActivity.EXTRA_DISEASE_NAME,
                    applicationContext.resources.getString(R.string.malaria_name)
                )
                .putExtra(MaskActivity.EXTRA_IMAGE_NAME, tempFile.absolutePath)
                .putExtra(MaskActivity.EXTRA_MASK_NAME, "mask")
        )
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
        onView(withId(viewId)).check(
            ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(
                    visibility
                )
            )
        )
    }

    private fun perform(viewId: Int, action: ViewAction): ViewInteraction {
        return onView(withId(viewId)).perform(action)
    }

    @Test
    fun shouldKeepMaskOnRotation() {
        startActivity()

        perform(R.id.photo_mask_view, swipeLeft())

        val captureBeforeRotation = capturePhotoMaskView()

        rotateAndWaitViewDisplay(Orientation.LANDSCAPE, R.id.photo_mask_view)

        rotateAndWaitViewDisplay(Orientation.PORTRAIT, R.id.photo_mask_view)

        val captureAfterRotation = capturePhotoMaskView()

        assertTrue(captureAfterRotation.bitmap.sameAs(captureBeforeRotation.bitmap))
    }

    @Test
    fun shouldKeepUndoAndRedoOnRotation() {
        startActivity()

        perform(R.id.photo_mask_view, swipeLeft())
        perform(R.id.photo_mask_view, swipeUpFromCenter())

        perform(R.id.undo_btn, click())

        rotateAndWaitViewDisplay(Orientation.LANDSCAPE, R.id.photo_mask_view)

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        rotateAndWaitViewDisplay(Orientation.PORTRAIT, R.id.photo_mask_view)

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)
    }

    private fun capturePhotoMaskView() = Screenshot.capture(
        activityTestRule.activity.findViewById<View>(
            R.id.photo_mask_view
        )
    )

    @Test
    fun shouldUndoAndRedoProperly() {
        startActivity()

        checkIsInvisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureStart = capturePhotoMaskView()

        perform(R.id.photo_mask_view, swipeLeft())

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureFirstPath = capturePhotoMaskView()

        perform(R.id.photo_mask_view, swipeUpFromCenter())

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureSecondPath = capturePhotoMaskView()

        perform(R.id.undo_btn, click())

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        assertTrue(capturePhotoMaskView().bitmap.sameAs(captureFirstPath.bitmap))

        perform(R.id.undo_btn, click())

        checkIsInvisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        assertTrue(capturePhotoMaskView().bitmap.sameAs(captureStart.bitmap))

        perform(R.id.redo_btn, click())

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        assertTrue(capturePhotoMaskView().bitmap.sameAs(captureFirstPath.bitmap))

        perform(R.id.redo_btn, click())

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        assertTrue(capturePhotoMaskView().bitmap.sameAs(captureSecondPath.bitmap))
    }

    @Test
    fun shouldUndoAndRedoProperlyWithRotation() {
        startActivity()

        checkIsInvisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureStart = capturePhotoMaskView()

        perform(R.id.photo_mask_view, swipeLeft())

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureFirstPath = capturePhotoMaskView()

        rotateAndWaitViewDisplay(Orientation.LANDSCAPE, R.id.photo_mask_view)
        perform(R.id.photo_mask_view, swipeUpFromCenter())
        rotateAndWaitViewDisplay(Orientation.PORTRAIT, R.id.photo_mask_view)

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        val captureSecondPath = capturePhotoMaskView()

        rotateAndWaitViewDisplay(Orientation.LANDSCAPE, R.id.photo_mask_view)
        perform(R.id.undo_btn, click())
        rotateAndWaitViewDisplay(Orientation.PORTRAIT, R.id.photo_mask_view)

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

         assertTrue(capturePhotoMaskView().bitmap.sameAs(captureFirstPath.bitmap))

        perform(R.id.undo_btn, click())

        checkIsInvisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        assertTrue(capturePhotoMaskView().bitmap.sameAs(captureStart.bitmap))

        rotateAndWaitViewDisplay(Orientation.LANDSCAPE, R.id.photo_mask_view)
        perform(R.id.redo_btn, click())
        rotateAndWaitViewDisplay(Orientation.PORTRAIT, R.id.photo_mask_view)

        checkIsVisible(R.id.undo_btn)
        checkIsVisible(R.id.redo_btn)

        assertTrue(capturePhotoMaskView().bitmap.sameAs(captureFirstPath.bitmap))

        perform(R.id.redo_btn, click())

        checkIsVisible(R.id.undo_btn)
        checkIsInvisible(R.id.redo_btn)

        assertTrue(capturePhotoMaskView().bitmap.sameAs(captureSecondPath.bitmap))
    }

    // this makes swipe from the center of the picture view instead of bottom
    // fixes tests running locally
    private fun swipeUpFromCenter(): ViewAction {
        return ViewActions.actionWithAssertions(
            GeneralSwipeAction(
                Swipe.FAST,
                GeneralLocation.VISIBLE_CENTER,
                GeneralLocation.TOP_CENTER,
                Press.FINGER
            )
        )
    }
}
