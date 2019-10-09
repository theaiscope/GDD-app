package net.aiscope.gdd_app.presentation

import android.content.Context
import com.nhaarman.mockito_kotlin.*
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SampleMetadata
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.metadata.MetadataPresenter
import net.aiscope.gdd_app.ui.metadata.MetadataView
import org.junit.Before
import org.junit.Test
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import kotlin.check


@RunWith(MockitoJUnitRunner::class)
class MetadataPresenterTest {

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
    fun before() {
        whenever(repository.current()).thenReturn(
            Sample(
                id = "id",
                healthFacility = "StPau",
                images = linkedSetOf(),
                disease = "malaria"
            )
        )

        val test = repository.current()
    }

    @Test
    fun shouldAskTheViewToSetTheForm() {
        subject.showScreen()
        verify(view).fillForm(any())
    }

    @Test
    fun shouldStoreMetadata() {
        val expected = SampleMetadata(SmearType.THICK, "specie1", "stage1")

        subject.save(SmearType.THICK, "specie1", "stage1")

        verify(remote).enqueue(check {
            assertEquals(it.metadata, expected)
        }, any())
        verify(repository).store(check {
            assertEquals(it.metadata, expected)
        })
    }
}
