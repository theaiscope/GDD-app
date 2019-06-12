package net.aiscope.gdd_app

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.ActivityTestRule
import org.hamcrest.Matchers.not
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test




class NewHealthFacilityActivityTest {

    @get:Rule
    var activityRule: ActivityTestRule<NewHealthFacilityActivity> = ActivityTestRule(NewHealthFacilityActivity::class.java)

    @Test
    fun shouldShowErrorMessageIfHealthFacilityIsEmpty() {
        onView(withId(R.id.text_health_facility_name_field))
            .perform(typeText(""))

        onView(withId(R.id.button_save_new_health_facility))
            .perform(ViewActions.click())

        onView(withText(R.string.error_message_field_empty)).inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldShowConfirmationMessageIfHealthFacilityIsValid() {
        onView(withId(R.id.text_health_facility_name_field))
            .perform(typeText("My Hospital"), closeSoftKeyboard())

        onView(withId(R.id.button_save_new_health_facility))
            .perform(ViewActions.click())

        onView(withText(R.string.confirmation_message_saved))
            .inRoot(withDecorView(not(activityRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shouldReturnToPreviousScreenIfClicked(){

        onView(withId(R.id.button_cancel_new_health_facility))
            .perform(ViewActions.click())

        assertTrue(activityRule.activity.isFinishing)
    }
}