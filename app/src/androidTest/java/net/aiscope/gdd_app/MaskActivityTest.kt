package net.aiscope.gdd_app

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.core.internal.deps.guava.collect.Iterables
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import androidx.test.runner.screenshot.ScreenCapture
import androidx.test.runner.screenshot.Screenshot
import net.aiscope.gdd_app.ui.main.MainActivity
import net.aiscope.gdd_app.ui.mask.MaskActivity
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep


@RunWith(AndroidJUnit4::class)
class MaskActivityTest {

    @get:Rule
    val mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun shouldKeepMaskOnRotation() {
        onView(
            withId(R.id.button_go_to_capture)
        ).perform(click())

        sleep(500)

        onView(
            withId(R.id.capture_image_button)
        ).perform(click())

        sleep(1000)

        onView(
            withId(R.id.mask_custom_view)
        ).perform(swipeLeft())

        sleep(500)

        getInstrumentation().waitForIdleSync()
        lateinit var currentActivity: Activity
        getInstrumentation().runOnMainSync {
            val activities =
                ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(Stage.RESUMED)
            currentActivity = Iterables.getOnlyElement(activities) as MaskActivity
        }

        val captureBeforeRotation: ScreenCapture = Screenshot.capture()

        currentActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        getInstrumentation().waitForIdleSync()

        currentActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        sleep(1000)

        val captureAfterRotation: ScreenCapture = Screenshot.capture()

        assertTrue(captureAfterRotation.bitmap.sameAs(captureBeforeRotation.bitmap))
    }
}
