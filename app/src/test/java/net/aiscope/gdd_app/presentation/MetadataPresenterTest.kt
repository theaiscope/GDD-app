package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.verify
import net.aiscope.gdd_app.ui.metadata.MetadataPresenter
import net.aiscope.gdd_app.ui.metadata.MetadataView
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class MetadataPresenterTest {

    @Mock
    lateinit var view: MetadataView

    @InjectMocks
    lateinit var subject: MetadataPresenter

    @Test
    fun shouldAskTheViewToSetTheForm() {
        subject.showScreen()
        verify(view).fillForm(any())
    }
}