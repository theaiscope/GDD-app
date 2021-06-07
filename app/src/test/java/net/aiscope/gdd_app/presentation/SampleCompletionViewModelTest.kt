package net.aiscope.gdd_app.presentation

import android.content.Context
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.aiscope.gdd_app.CoroutineTestRule
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SampleCompletionViewModelTest {
    companion object {
        private val sample = Sample("an id", "a facility", "a microscopist", "a disease")
        private val lastSample = sample.copy(microscopeQuality = MicroscopeQuality(true, 1500))
    }

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    @Mock
    private lateinit var repository: SampleRepository

    @Mock
    private lateinit var remoteStorage: RemoteStorage

    @Mock
    private lateinit var context: Context

    private lateinit var viewModel: SampleCompletionViewModel

    @Before
    fun before()  {
        coroutinesTestRule.runBlockingTest {
            whenever(context.getString(R.string.spinner_empty_option))
                .thenReturn("- select -")
            whenever(context.getString(R.string.malaria_species_p_falciparum))
                .thenReturn("P. falciparum")
            whenever(repository.current()).thenReturn(sample)

            viewModel = SampleCompletionViewModel(repository, remoteStorage, context)
        }
    }

    @Test
    fun `Should set defaults if no previously saved sample`() {
        coroutinesTestRule.runBlockingTest {
            whenever(repository.lastSaved()).thenReturn(null)
            viewModel.initVM()

            // Should be a nicer way to do this, really
            Thread.sleep(1000)

            assertEquals(null, viewModel.smearTypeId)
            assertEquals("a disease", viewModel.disease)
        }
    }

    @Test
    fun `Should set values from previously saved sample`() {
        coroutinesTestRule.runBlockingTest {
            whenever(repository.lastSaved()).thenReturn(lastSample)
            viewModel.initVM()

            // Should be a nicer way to do this, really
            Thread.sleep(1000)

            assertEquals(true, viewModel.microscopeDamaged)
            assertEquals(1500, viewModel.microscopeMagnification)
        }
    }

}