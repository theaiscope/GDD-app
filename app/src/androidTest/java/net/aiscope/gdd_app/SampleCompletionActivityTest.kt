package net.aiscope.gdd_app

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.azimolabs.conditionwatcher.ConditionWatcher
import com.azimolabs.conditionwatcher.Instruction
import com.nhaarman.mockito_kotlin.whenever
import net.aiscope.gdd_app.model.Microscopist
import net.aiscope.gdd_app.repository.MicroscopistRepository
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class SampleCompletionActivityTest {

    //TODO: launch or no?
    private val activityTestRule = ActivityTestRule(SampleCompletionActivity::class.java, true, false)

    @Mock
    private lateinit var microscopistRepository: MicroscopistRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this) // this is needed for initialization of mocks, if you use @Mock
    }

    private fun startActivity() {
        activityTestRule.launchActivity(
            Intent(Intent.ACTION_MAIN)
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
    fun shouldTrainFirstTime() {
        var mockMicro: Microscopist = Microscopist("1", true, false)
        whenever(microscopistRepository?.load()).thenReturn(mockMicro)
        startActivity()
    }

    @Test
    fun shouldNotTrainSecondTime() {
        var mockMicro: Microscopist = Microscopist("1", true, true)
        whenever(microscopistRepository?.load()).thenReturn(mockMicro)
        startActivity()
    }

}
