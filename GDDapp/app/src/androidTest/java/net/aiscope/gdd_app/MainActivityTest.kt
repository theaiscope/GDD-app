package net.aiscope.gdd_app

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun shouldDisplayWelcomeMessage() {
        Espresso.onView(ViewMatchers.withText("Welcome to AI Scope application"))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun buttonShouldRedirectToNewHealthFacilityActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.button_go_to_new_health_facility))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.text_health_facility_name_hint))
            .check(ViewAssertions.matches(ViewMatchers.withText("Enter your Health Facility Name")))
    }

}