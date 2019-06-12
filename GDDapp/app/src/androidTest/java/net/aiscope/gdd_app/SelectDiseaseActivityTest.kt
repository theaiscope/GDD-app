package net.aiscope.gdd_app

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Rule
import org.junit.Test


class SelectDiseaseActivityTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(SelectDiseaseActivity::class.java)

    @Test
    fun spinnerShouldDisplayDiseasesList () {
        Espresso.onView(ViewMatchers.withId(R.id.spinner_diseases))
            .perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Malaria"))).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Tuberculosis"))).perform(click())
    }

    @Test
    fun shouldRedirectToCaptureImageActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.button_capture_image_select_disease))
            .perform(click())

        intended(hasComponent(CaptureImageActivity::class.java.name))
    }
}