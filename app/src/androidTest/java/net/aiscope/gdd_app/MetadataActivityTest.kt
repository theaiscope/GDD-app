package net.aiscope.gdd_app

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
import org.hamcrest.CoreMatchers.anything
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test


class MetadataActivityTest {

    @get:Rule
    val intentsMetadataTestRule = IntentsTestRule(MetadataActivity::class.java)

    @Test
    @Ignore("needs to have firebase auth current user stubbed")
    fun thickSmearTypeIsPreselected () {
        Espresso.onView(ViewMatchers.withText(R.string.metadata_blood_smear_thick))
            .check(matches(isChecked()))
    }

    @Test
    @Ignore("needs to have firebase auth current user stubbed")
    fun shouldStoreData() {
        // Select bloodType
        Espresso.onView(ViewMatchers.withText(R.string.metadata_blood_smear_thin)).perform(click())

        Espresso.onView(ViewMatchers.withId(R.id.metadata_species_spinner)).perform(click())
        Espresso.onData(anything()).atPosition(1).perform(click())

        // Save button click
        Espresso.onView(ViewMatchers.withText("SAVE SAMPLE")).perform(click())
    }
}
