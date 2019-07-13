package net.aiscope.gdd_app

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.instanceOf
import org.junit.Assert
import org.junit.Rule
import org.junit.Test


class MetadataActivityTest {

    @get:Rule
    val intentsMetadataTestRule = IntentsTestRule(MetadataActivity::class.java)

    @Test
    fun displayBloodtypeOptions () {
        Espresso.onView(ViewMatchers.withText(R.string.metadata_blood_smear_title)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("thin"))).perform(click())
    }

    @Test
    fun shouldShowErrorMessageIfNoBloodTypeSelected() {


        Espresso.onView(ViewMatchers.withText(R.string.metadata_add)).perform(click())

        Espresso.onView(ViewMatchers.withText(R.string.metadata_invalid_form))
            .inRoot(RootMatchers.withDecorView(Matchers.not(intentsMetadataTestRule.activity.window.decorView)))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun shouldStoreData() {
        // Select bloodType
        Espresso.onView(ViewMatchers.withText(R.string.metadata_blood_smear_title)).perform(click())
        onData(allOf(`is`(instanceOf(String::class.java)), `is`("thin"))).perform(click())

        // Save button click
        Espresso.onView(ViewMatchers.withText(R.string.metadata_add)).perform(click())
    }

}