package net.aiscope.gdd_app

import android.view.View
import androidx.test.espresso.Espresso
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
import net.aiscope.gdd_app.ui.metadata.MetadataView
import net.aiscope.gdd_app.ui.metadata.ViewStateModel
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.Matcher
import org.hamcrest.Matchers
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test


class MetadataActivityTest {

    @get:Rule
    val intentsMetadataTestRule = IntentsTestRule(MetadataActivity::class.java)

    @Test
    fun thickSmearTypeIsPreselected () {
        Espresso.onView(ViewMatchers.withText(R.string.metadata_blood_smear_thick))
            .check(matches(isChecked()))
    }

    @Test
    @Ignore("Blood type is preselected")
    fun shouldShowErrorMessageIfNoBloodTypeSelected() {
        Espresso.onView(ViewMatchers.withText(R.string.metadata_save_sample)).perform(click())

        Espresso.onView(ViewMatchers.withText(R.string.metadata_invalid_form))
            .inRoot(RootMatchers.withDecorView(Matchers.not(intentsMetadataTestRule.activity.window.decorView)))
            .check(matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun shouldStoreData() {
        // Select bloodType
        Espresso.onView(ViewMatchers.withText(R.string.metadata_blood_smear_thin)).perform(click())
//        onData(allOf(`is`(instanceOf(String::class.java)), `is`("thin"))).perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.metadata_species_spinner)).perform(click())
        Espresso.onData(anything()).atPosition(1).perform(click());

        Espresso.onView(ViewMatchers.withId(R.id.metadata_stage_spinner)).perform(click())
        Espresso.onData(anything()).atPosition(1).perform(click());

        // Save button click
        Espresso.onView(ViewMatchers.withText("SAVE SAMPLE")).perform(click())
    }
}
