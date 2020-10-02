package net.aiscope.gdd_app.presentation

import android.content.Context
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.aiscope.gdd_app.CoroutineTestRule
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.model.Images
import net.aiscope.gdd_app.model.MalariaSpecies
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.metadata.MetadataMapper
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
class MetadataPresenterTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val view: MetadataView = mock()
    private val repository: SampleRepository = mock()
    private val remote: RemoteStorage = mock()
    private val context: Context = mock()
    private val metadataMapper: MetadataMapper = mock()
    private val subject = MetadataPresenter(view, repository, metadataMapper, remote, context)

    @Before
    fun before() = coroutinesTestRule.runBlockingTest {
        whenever(repository.current()).thenReturn(
            Sample(
                id = "id",
                healthFacility = "StPau",
                microscopist = "a microscopist",
                images = Images(),
                disease = "malaria"
            )
        )

        repository.current()

        whenever(metadataMapper.getSmearType(R.id.metadata_blood_smear_thick))
            .thenReturn(SmearType.THICK)
        whenever(metadataMapper.getSmearTypeId(SmearType.THIN))
            .thenReturn(R.id.metadata_blood_smear_thin)
        whenever(metadataMapper.getSpecies(context, "P. vivax"))
            .thenReturn(MalariaSpecies.P_VIVAX)
        whenever(metadataMapper.getSpeciesValue(context, MalariaSpecies.P_OVALE))
            .thenReturn("P. ovale")
    }

    @Test
    fun shouldAskTheViewToSetTheForm() = coroutinesTestRule.runBlockingTest {
        subject.showScreen()
        verify(view).fillForm(any())
    }

    @Test
    fun shouldStoreMetadata() = coroutinesTestRule.runBlockingTest {
        whenever(repository.store(any())).then{i -> i.arguments[0]}

        val comments = "some-comment"
        val expected = SampleMetadata(
            SmearType.THICK, MalariaSpecies.P_VIVAX, comments
        )

        subject.save(R.id.metadata_blood_smear_thick, "P. vivax", comments)

        verify(remote).enqueue(check {
            assertEquals(it.metadata, expected)
        }, any())
        verify(repository).store(check {
            assertEquals(it.metadata, expected)
        })
    }

    @Test
    fun shouldDefaultToLastMetadata() = coroutinesTestRule.runBlockingTest{
        val expectedSmearType = R.id.metadata_blood_smear_thin
        val expectedSpecies = "P. ovale"

        whenever(repository.lastSaved()).thenReturn(
            Sample(
                id = "idlast",
                healthFacility = "StPau",
                microscopist = "a microscopist",
                images = Images(),
                disease = "malaria",
                createdOn = java.util.Calendar.getInstance(),
                metadata = SampleMetadata(
                    SmearType.THIN,
                    MalariaSpecies.P_OVALE
                )
            )
        )

        argumentCaptor<ViewStateModel>().apply{
            subject.showScreen()
            verify(view).fillForm(capture())
            assertEquals(expectedSmearType, allValues[0].smearTypeId)
            assertEquals(expectedSpecies, allValues[0].speciesValue)
        }
    }
}
