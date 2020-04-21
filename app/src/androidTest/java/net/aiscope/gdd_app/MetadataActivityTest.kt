package net.aiscope.gdd_app

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
import org.hamcrest.CoreMatchers.anything
import org.hamcrest.Matchers
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test

class MetadataActivityTest {

    @get:Rule
    val intentsMetadataTestRule = IntentsTestRule(MetadataActivity::class.java)

    @Test
    @Ignore("needs to have firebase auth current user stubbed")
    fun thickSmearTypeIsPreselected () {
        onView(withText(R.string.metadata_blood_smear_thick))
            .check(matches(isChecked()))
    }

    @Test
    @Ignore("Blood type is preselected")
    fun shouldShowErrorMessageIfNoBloodTypeSelected() {
        onView(withText(R.string.metadata_save_sample)).perform(click())

        onView(withText(R.string.metadata_invalid_form))
            .inRoot(RootMatchers.withDecorView(Matchers.not(intentsMetadataTestRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    @Ignore("needs to have firebase auth current user stubbed")
    fun shouldStoreData() {
        // Select bloodType
        onView(withText(R.string.metadata_blood_smear_thin)).perform(click())
//        onData(allOf(`is`(instanceOf(String::class.java)), `is`("thin"))).perform(click())

        onView(ViewMatchers.withId(R.id.metadata_species_spinner)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        onView(ViewMatchers.withId(R.id.metadata_stage_spinner)).perform(click())
        onData(anything()).atPosition(1).perform(click())

        // Save button click
        onView(withText("SAVE SAMPLE")).perform(click())
    }
}
