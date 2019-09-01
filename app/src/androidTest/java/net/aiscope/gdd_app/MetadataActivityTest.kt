package net.aiscope.gdd_app

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
import org.hamcrest.Matchers
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test


class MetadataActivityTest {

    @get:Rule
    val intentsMetadataTestRule = IntentsTestRule(MetadataActivity::class.java)

    @Test
    @Ignore("Repository can't be mocked ATM")
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
    @Ignore("Repository can't be mocked ATM")
    fun shouldStoreData() {
        // Select bloodType
        Espresso.onView(ViewMatchers.withText(R.string.metadata_blood_smear_thin)).perform(click())
//        onData(allOf(`is`(instanceOf(String::class.java)), `is`("thin"))).perform(click())

        // Save button click
        Espresso.onView(ViewMatchers.withText("SAVE SAMPLE")).perform(click())
    }

}
