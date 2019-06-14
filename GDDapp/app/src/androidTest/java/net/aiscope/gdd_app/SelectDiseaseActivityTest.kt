package net.aiscope.gdd_app

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseaseActivity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Assert
import org.junit.Rule
import org.junit.Test


class SelectDiseaseActivityTest {

    @get:Rule
    val intentsSelectDiseaseTestRule = IntentsTestRule(SelectDiseaseActivity::class.java)

    @Test
    fun spinnerShouldDisplayDiseasesList () {
        Espresso.onView(ViewMatchers.withId(R.id.spinner_diseases))
            .perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Malaria"))).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Tuberculosis"))).perform(click())
    }

    @Test
    fun shouldRedirectToCaptureImageActivity() {
        selectFirstItem()

        Espresso.onView(ViewMatchers.withId(R.id.button_capture_image_select_disease))
            .perform(click())

        intended(hasComponent(CaptureImageActivity::class.java.name))
    }

    @Test
    fun shouldShowErrorMessageIfDiseaseIsNotSelected() {

        Espresso.onView(ViewMatchers.withId(R.id.button_capture_image_select_disease))
            .perform(click())

        Espresso.onView(ViewMatchers.withText(R.string.error_message_field_empty))
            .inRoot(RootMatchers.withDecorView(Matchers.not(intentsSelectDiseaseTestRule.activity.window.decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun shouldShowConfirmationMessageIfDiseaseIsValid() {
        selectFirstItem()

        Espresso.onView(ViewMatchers.withId(R.id.button_capture_image_select_disease))
            .perform(click())

        Espresso.onView(ViewMatchers.withText(R.string.confirmation_message_saved))
            .inRoot(RootMatchers.withDecorView(Matchers.not(intentsSelectDiseaseTestRule.activity.window.decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    private fun selectFirstItem() {
        Espresso.onView(ViewMatchers.withId(R.id.spinner_diseases)).perform(click())

        onData(allOf(`is`(instanceOf(String::class.java)))).atPosition(1).perform(click())
    }

    @Test
    fun shouldFinishActivityIfCancelButtonIsClicked() {
        Espresso.onView(ViewMatchers.withId(R.id.button_back_select_disease))
            .perform(click())

        Assert.assertTrue(intentsSelectDiseaseTestRule.activity.isFinishing)
    }
}