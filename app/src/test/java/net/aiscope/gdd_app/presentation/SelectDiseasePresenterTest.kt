package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.*
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseasePresenter
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseaseView
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class SelectDiseasePresenterTest {

    @Mock
    lateinit var view: SelectDiseaseView

    @Mock
    lateinit var repository: SampleRepository

    @InjectMocks
    lateinit var subject: SelectDiseasePresenter

    @Test
    fun `should store a disease with a name in the repository and call success toast on the view`() {
        whenever(repository.create()).thenReturn(Sample("id", "hospital"))
        subject.saveDisease(DISEASE_NAME)

        argumentCaptor<Sample>().apply {
            verify(repository).store(capture())
            assert(firstValue.disease == DISEASE_NAME)
            verify(view).showSuccessToast()
        }
    }

    @Test
    fun `should call failure toast on the view is name is empty`() {
        subject.saveDisease("")

        verify(repository, times(0)).store(any())
        verify(view).showFailureToast()
    }

    @Test
    fun `should call failure toast on the view is name is blank`() {
        subject.saveDisease("    ")

        verify(repository, times(0)).store(any())
        verify(view).showFailureToast()
    }

    companion object {
        private const val DISEASE_NAME = "testDisease"
    }
}