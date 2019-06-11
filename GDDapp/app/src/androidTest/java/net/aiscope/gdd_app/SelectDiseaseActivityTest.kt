package net.aiscope.gdd_app

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.rule.ActivityTestRule
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseaseActivity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Rule
import org.junit.Test


class SelectDiseaseActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<SelectDiseaseActivity> = ActivityTestRule(
        SelectDiseaseActivity::class.java)

    @Test
    fun spinnerShouldDisplayDiseasesList () {
        Espresso.onView(ViewMatchers.withId(R.id.spinner_diseases))
            .perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Malaria"))).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("Tuberculosis"))).perform(click())
    }
}