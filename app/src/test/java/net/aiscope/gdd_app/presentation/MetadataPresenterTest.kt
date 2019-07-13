package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.metadata.ListItem
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

    @InjectMocks
    lateinit var subject: MetadataPresenter

    @Before
    fun before() {
        whenever(repository.current()).thenReturn(Sample("id", "StPau"))
    }

    @Test
    fun shouldAskTheViewToSetTheForm() {
        subject.showScreen()
        verify(view).fillForm(any())
    }

    @Test
    fun shouldStoreMetadata() {
        subject.save(listOf(ListItem(1, "thick")))
        verify(repository).store(any())
    }
}