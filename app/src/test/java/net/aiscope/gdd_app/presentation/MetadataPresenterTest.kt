package net.aiscope.gdd_app.presentation

import android.content.Context
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.aiscope.gdd_app.CoroutineTestRule
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.MalariaStage
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.metadata.MetadataPresenter
import net.aiscope.gdd_app.ui.metadata.MetadataView
import net.aiscope.gdd_app.ui.metadata.ViewStateModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class MetadataPresenterTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Mock
    lateinit var view: MetadataView

    @Mock
    lateinit var repository: SampleRepository

    @Mock
    lateinit var remote: RemoteStorage

    @Mock
    lateinit var context: Context


    @InjectMocks
    lateinit var subject: MetadataPresenter

    @Before
    fun before() = coroutinesTestRule.runBlockingTest {
        whenever(repository.current()).thenReturn(
            Sample(
                id = "id",
                healthFacility = "StPau",
                microscopist = "a microscopist",
                images = linkedSetOf(),
                disease = "malaria"
            )
        )

        repository.current()
    }

    @Test
    fun shouldAskTheViewToSetTheForm() = coroutinesTestRule.runBlockingTest {
        subject.showScreen()
        verify(view).fillForm(any())
    }

    @Test
    fun shouldStoreMetadata() = coroutinesTestRule.runBlockingTest {
        val expected = SampleMetadata(
            SmearType.THICK, MalariaSpecies.P_VIVAX,
            MalariaStage.TROPHOZOITE
        )

        subject.save(SmearType.THICK, MalariaSpecies.P_VIVAX, MalariaStage.TROPHOZOITE)

        verify(remote).enqueue(check {
            assertEquals(it.metadata, expected)
        }, any())
        verify(repository).store(check {
            assertEquals(it.metadata, expected)
        })
    }

    @Test
    fun shouldDefaultToLastMetadata() = coroutinesTestRule.runBlockingTest{
        val expectedMetadata = SampleMetadata(
            SmearType.THIN,
            MalariaSpecies.P_OVALE,
            MalariaStage.TROPHOZOITE
        )

        whenever(repository.last()).thenReturn(
            Sample(
                id = "idlast",
                healthFacility = "StPau",
                microscopist = "a microscopist",
                images = linkedSetOf(),
                disease = "malaria",
                createdOn = java.util.Calendar.getInstance(),
                metadata = expectedMetadata
            )
        )

        argumentCaptor<ViewStateModel>().apply{
            subject.showScreen()
            verify(view).fillForm(capture())
            assertEquals(allValues[0].sampleMetadata, expectedMetadata)
        }
    }
}
