package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.aiscope.gdd_app.CoroutineTestRule
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.main.SelectDiseasePresenter
import net.aiscope.gdd_app.ui.main.SelectDiseaseView
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner


@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class SelectDiseasePresenterTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Mock
    lateinit var view: SelectDiseaseView

    @Mock
    lateinit var repository: SampleRepository

    @InjectMocks
    lateinit var subject: SelectDiseasePresenter

    @Test
    fun `should store a disease with a name in the repository and call success toast on the view`() =
        coroutinesTestRule.runBlockingTest {
            whenever(repository.create()).thenReturn(Sample("id", "hospital", "a microscopist"))
            subject.saveDisease(DISEASE_NAME)

            argumentCaptor<Sample>().apply {
                verify(repository).store(capture())
                assert(firstValue.disease == DISEASE_NAME)
                verify(view).showSuccessToast()
            }
        }

    @Test
    fun `should call failure toast on the view is name is empty`() =
        coroutinesTestRule.runBlockingTest {
            subject.saveDisease("")

            verify(repository, times(0)).store(any())
            verify(view).showFailureToast()
        }

    @Test
    fun `should call failure toast on the view is name is blank`() =
        coroutinesTestRule.runBlockingTest {
            subject.saveDisease("    ")

            verify(repository, times(0)).store(any())
            verify(view).showFailureToast()
        }

    companion object {
        private const val DISEASE_NAME = "testDisease"
    }
}
