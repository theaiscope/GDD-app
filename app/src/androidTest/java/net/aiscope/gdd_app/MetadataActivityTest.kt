package net.aiscope.gdd_app

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import com.nhaarman.mockito_kotlin.whenever
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.metadata.MetadataActivity
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import java.io.File


class MetadataActivityTest {

    @get:Rule val rule = espressoDaggerMockRule()

    @get:Rule
    val intentsMetadataTestRule = IntentsTestRule(MetadataActivity::class.java, false, false)

    @Mock
    lateinit var repository: SampleRepository

    @Before
    fun setUp() {
        whenever(repository.current()).thenReturn(
            Sample(
                id = "id",
                healthFacility = "StPau",
                images = linkedSetOf(File("/data/data/net.aiscope.gdd_app.debug/files/140cc3b5-70d0-4421-b715-8db2a2e7f031_image_0.jpg"))
            )
        )
        intentsMetadataTestRule.launchActivity(Intent())
    }

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

}
