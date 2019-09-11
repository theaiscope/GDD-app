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

        // Save button click
        Espresso.onView(ViewMatchers.withText("SAVE SAMPLE")).perform(click())
    }

    // TODO: Finish the UI test
    /*
    @Test
    fun shouldShowSpeciesAndStageIfMalaria() {
        val modelState = ViewStateModel("Malaria", emptyList(), emptyList())

        Espresso.onView(ViewMatchers.withId(R.id.metadata_view))
            .perform(callMethod<MetadataView>("call doSomething() method") {
                it.fillForm(modelState)
            })

        Espresso.onView(ViewMatchers.withId(R.id.metadata_species_divider)).check(matches(ViewMatchers.isDisplayed()))
    }

    private fun <T>callMethod(message: String = "", someMethod: (view: T) -> Unit): ViewAction {
        return object: ViewAction {
            override fun getDescription(): String {
                return if(message.isBlank()) someMethod.toString() else message
            }

            override fun getConstraints(): Matcher<View> {
                return isEnabled()
            }

            override fun perform(uiController: UiController?, view: View?) {
                someMethod(view!! as T)
            }
        }
    }*/

}
