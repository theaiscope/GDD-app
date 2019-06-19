package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import net.aiscope.gdd_app.model.Disease
import net.aiscope.gdd_app.repository.HospitalRepository
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
    lateinit var repository: HospitalRepository

    @InjectMocks
    lateinit var subject: SelectDiseasePresenter

    private val DISEASE_NAME = "testDisease"

    @Test
    fun `should store a disease with a name in the repository and call success toast on the view`() {
        subject.saveDisease(DISEASE_NAME)

        argumentCaptor<Disease>().apply {
            verify(repository).store(capture())
            assert(firstValue.name == DISEASE_NAME)
            verify(view).showSuccessToast()
        }
    }

    @Test
    fun `should call failure toast on the view is name is empty`() {
        subject.saveDisease("")

        verify(repository, times(0)).store(any<Disease>())
        verify(view).showFailureToast()
    }

    @Test
    fun `should call failure toast on the view is name is blank`() {
        subject.saveDisease("    ")

        verify(repository, times(0)).store(any<Disease>())
        verify(view).showFailureToast()
    }
}