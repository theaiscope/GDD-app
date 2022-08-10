package net.aiscope.gdd_app.presentation

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.aiscope.gdd_app.CoroutineTestRule
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.SampleAge
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.MicroscopeQuality
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SamplePreparation
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.model.WaterType
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.MicroscopistRepository
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.repository.SampleRepositoryFirestore
import net.aiscope.gdd_app.ui.sample_completion.SampleCompletionViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SampleCompletionViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    companion object {
        private val sample = Sample("an id", "a facility", "a microscopist", "a disease")
        private val lastSample = sample.copy(
            id = "last ID",
            microscopeQuality = MicroscopeQuality(true, 1500),
            preparation = SamplePreparation(
                WaterType.TAP,
                usesGiemsa = false,
                giemsaFP = false,
                usesPbs = true,
                reusesSlides = true,
                sampleAge = SampleAge.OLD
            ),
            metadata = SampleMetadata(
                smearType = SmearType.THICK,
                species = MalariaSpecies.P_FALCIPARUM,
                comments = "Should not show"
            )
        )
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

    @Mock
    private lateinit var microscopistRepository: MicroscopistRepository

    @Mock
    private lateinit var sampleRepositoryFirestore: SampleRepositoryFirestore

    @Before
    fun before() {
        coroutinesTestRule.runBlockingTest {
            whenever(context.getString(R.string.spinner_empty_option))
                .thenReturn("- select -")
            whenever(context.getString(R.string.malaria_species_p_falciparum))
                .thenReturn("P. falciparum")
            whenever(context.getString(R.string.malaria_species_p_vivax))
                .thenReturn("P. vivax")
            whenever(context.getString(R.string.water_type_tap))
                .thenReturn("Tap")
            whenever(context.getString(R.string.water_type_well))
                .thenReturn("Well")
            whenever(context.getString(R.string.sample_age_old))
                .thenReturn("Old")
            whenever(context.getString(R.string.sample_age_fresh))
                .thenReturn("Fresh")

            whenever(repository.current()).thenReturn(sample)

            viewModel = SampleCompletionViewModel(
                repository, remoteStorage, context,
                microscopistRepository, sampleRepositoryFirestore
            )
        }
    }

    @Test
    fun `Should set defaults if no previously saved sample`() {
        coroutinesTestRule.runBlockingTest {
            whenever(repository.lastSaved()).thenReturn(null)
            viewModel.initVM()

            // Should be a nicer way to do this, really
            Thread.sleep(1000)

            assertEquals("a disease", viewModel.disease)

            assertEquals(false, viewModel.microscopeDamaged)
            assertEquals(1000, viewModel.microscopeMagnification)

            assertEquals("- select -", viewModel.waterType)
            assertEquals(true, viewModel.usesGiemsa)
            assertEquals(true, viewModel.giemsaFP)
            assertEquals(true, viewModel.usesPbs)
            assertEquals(false, viewModel.reusesSlides)
            assertEquals("- select -", viewModel.sampleAge)

            assertEquals(null, viewModel.smearTypeId)
            assertEquals(null, viewModel.speciesValue)
            assertEquals(null, viewModel.comments)
        }
    }

    @Test
    fun `Should set values from previously saved sample`() {
        coroutinesTestRule.runBlockingTest {
            whenever(repository.lastSaved()).thenReturn(lastSample)
            viewModel.initVM()

            // Should be a nicer way to do this, really
            Thread.sleep(1000)

            //Taken from current
            assertEquals("a disease", viewModel.disease)

            //Default
            assertEquals(null, viewModel.comments)

            //Taken from previous
            assertEquals(true, viewModel.microscopeDamaged)
            assertEquals(1500, viewModel.microscopeMagnification)

            assertEquals("Tap", viewModel.waterType)
            assertEquals(false, viewModel.usesGiemsa)
            assertEquals(false, viewModel.giemsaFP)
            assertEquals(true, viewModel.usesPbs)
            assertEquals(true, viewModel.reusesSlides)
            assertEquals("Old", viewModel.sampleAge)

            assertEquals(R.id.metadata_blood_smear_thick, viewModel.smearTypeId)
            assertEquals("P. falciparum", viewModel.speciesValue)

            //Make sure we have the right current
            assertEquals("an id", viewModel.getCurrentSample().id)
        }
    }

    @Test
    fun `Should store updated sample in repository`() {
        coroutinesTestRule.runBlockingTest {

            viewModel.sampleAge = "Old"
            viewModel.waterType = "Tap"
            viewModel.smearTypeId = R.id.metadata_blood_smear_thin
            viewModel.speciesValue = "P. vivax"
            viewModel.comments = "Random comment"
            viewModel.usesGiemsa = false
            viewModel.giemsaFP = true
            viewModel.microscopeDamaged = true
            viewModel.microscopeMagnification = 1700
            viewModel.reusesSlides = true
            viewModel.usesPbs = false
            viewModel.disease = "test disease"

            viewModel.save()

            Thread.sleep(1000)

            argumentCaptor<Sample>().apply {
                verify(repository).store(capture())
                assertEquals(SampleAge.OLD, firstValue.preparation?.sampleAge)
                assertEquals(WaterType.TAP, firstValue.preparation?.waterType)
                assertEquals(SmearType.THIN, firstValue.metadata.smearType)
                assertEquals(MalariaSpecies.P_VIVAX, firstValue.metadata.species)
                assertEquals("Random comment", firstValue.metadata.comments)
                assertEquals(false, firstValue.preparation?.usesGiemsa)
                assertEquals(true, firstValue.preparation?.giemsaFP)
                assertEquals(true, firstValue.microscopeQuality?.isDamaged)
                assertEquals(1700, firstValue.microscopeQuality?.magnification)
                assertEquals(true, firstValue.preparation?.reusesSlides)
                assertEquals(false, firstValue.preparation?.usesPbs)

                //Disease value does not get written back to sample, so it should be the original value
                assertEquals("a disease", firstValue.disease)


            }
        }
    }

    @Test
    fun `Should upload updated sample to remote storage`() {
        coroutinesTestRule.runBlockingTest {
            //So we expect the values returned here to be in the one that gets enqueued
            whenever(repository.store(any())).thenReturn(lastSample)

            viewModel.sampleAge = "Fresh"
            viewModel.waterType = "Well"
            viewModel.smearTypeId = R.id.metadata_blood_smear_thin
            viewModel.speciesValue = "P. vivax"
            viewModel.comments = "Random comment"

            viewModel.save()

            Thread.sleep(1000)

            argumentCaptor<Sample>().apply {
                verify(remoteStorage).enqueue(capture(), any())
                assertEquals("last ID", firstValue.id)
                assertEquals(SampleAge.OLD, firstValue.preparation?.sampleAge)
                assertEquals(WaterType.TAP, firstValue.preparation?.waterType)
                assertEquals(SmearType.THICK, firstValue.metadata.smearType)
                assertEquals(MalariaSpecies.P_FALCIPARUM, firstValue.metadata.species)
                assertEquals("Should not show", firstValue.metadata.comments)
                assertEquals("a facility", firstValue.healthFacility)
            }
        }
    }
}
