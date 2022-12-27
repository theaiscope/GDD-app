package net.aiscope.gdd_app

import android.Manifest
import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withSpinnerText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.rule.GrantPermissionRule
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.main.MainActivity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @get:Rule
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Test
    fun shouldDisplayWelcomeMessage() {
        Espresso.onView(ViewMatchers.withText(R.string.welcome_msg))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun spinnerShouldDisplayDiseasesList() {
        Espresso.onView(ViewMatchers.withId(R.id.dropdown_select_disease))
            .perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Malaria"))).perform(click())
    }

    @Test
    fun spinnerShouldHaveMalariaPreselectedAsFirstOption() {
        Espresso.onView(ViewMatchers.withId(R.id.dropdown_select_disease))
            .perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Malaria"))).perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.dropdown_select_disease))
            .check(ViewAssertions.matches(withSpinnerText(`is`("Malaria"))))
    }

    @Test
    @Ignore("needs to have firebase auth current user stubbed")
    fun shouldRedirectToCaptureImageActivity() {
        selectFirstItem()

        Espresso.onView(ViewMatchers.withId(R.id.main_continue_button))
            .perform(click())

        intended(hasComponent(CaptureImageActivity::class.java.name))
    }

    @Test
    @Ignore("needs to have firebase auth current user stubbed")
    fun shouldShowConfirmationMessageIfDiseaseIsValid() {
        selectFirstItem()

        Espresso.onView(ViewMatchers.withId(R.id.main_continue_button))
            .perform(click())

        Espresso.onView(ViewMatchers.withText(R.string.confirmation_message_saved))
            .inRoot(RootMatchers.withDecorView(Matchers.not(activityRule.activity.window.decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun selectFirstItem() {
        Espresso.onView(ViewMatchers.withId(R.id.dropdown_select_disease)).perform(click())

        onData(allOf(`is`(instanceOf(String::class.java)))).atPosition(0).perform(click())
    }

    @Test
    fun shouldShowLogoutMenuOption() {
        Espresso.onView(ViewMatchers.withId(R.id.action_user))
            .perform(click())

        Espresso.onView(ViewMatchers.withText(R.string.text_logout))
            .inRoot(RootMatchers.withDecorView(Matchers.not(activityRule.activity.window.decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun shouldShowLogoutDialog() {
        Espresso.onView(ViewMatchers.withId(R.id.action_user))
            .perform(click())

        Espresso.onView(ViewMatchers.withText(R.string.text_logout))
            .inRoot(RootMatchers.withDecorView(Matchers.not(activityRule.activity.window.decorView)))
            .perform(click())

        Espresso.onView(ViewMatchers.withText(R.string.logout_flow_exit_dialog_message))
            .inRoot(RootMatchers.withDecorView(Matchers.not(activityRule.activity.window.decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
     fun shouldShowSuccessMessageWhenSampleSavedFromMetadataDialog() {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        val intent = Intent(targetContext, MainActivity::class.java)
        intent.putExtra("SAMPLE_SAVED", "OK")

        activityRule.launchActivity(intent)

       Espresso
           .onView(ViewMatchers.withSubstring(targetContext.resources.getString(R.string.metadata_snackbar_success)))
           .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun shouldNotShowSuccessMessageWhenNotComingFromMetadataDialog() {
        val targetContext = InstrumentationRegistry.getInstrumentation().targetContext

        val intent = Intent(targetContext, MainActivity::class.java)

        activityRule.launchActivity(intent)

        Espresso
            .onView(ViewMatchers.withSubstring(targetContext.resources.getString(R.string.metadata_snackbar_success)))
            .check(doesNotExist())
    }
}


