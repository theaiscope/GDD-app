package net.aiscope.gdd_app.presentation

import android.content.Context
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.model.SmearType
import net.aiscope.gdd_app.network.RemoteStorage
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.metadata.MetadataPresenter
import net.aiscope.gdd_app.ui.metadata.MetadataView
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


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
                images = linkedSetOf()
            )
        )
    }

    @Test
    fun shouldAskTheViewToSetTheForm() {
        subject.showScreen()
        verify(view).fillForm(any())
    }

    @Test
    fun shouldStoreMetadata() {
        subject.save(SmearType.THICK)
        verify(remote).enqueue(any(), any())
        verify(repository).store(any())
    }
}
