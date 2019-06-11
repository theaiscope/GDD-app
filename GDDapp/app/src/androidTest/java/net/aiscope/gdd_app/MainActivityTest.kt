package net.aiscope.gdd_app

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import net.aiscope.gdd_app.ui.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun shouldDisplayWelcomeMessage() {
        Espresso.onView(ViewMatchers.withText(R.string.welcome_msg))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun shouldRedirectToNewHealthFacilityActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.button_go_to_new_health_facility))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.text_health_facility_name_hint))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.text_health_facility_name_hint)))
    }

    @Test
    fun shouldRedirectToSelectDiseaseActivity() {
        Espresso.onView(ViewMatchers.withId(R.id.button_go_to_select_disease))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.text_select_disease_spinner_hint))
            .check(ViewAssertions.matches(ViewMatchers.withText(R.string.text_select_disease_spinner_hint)))
    }

}