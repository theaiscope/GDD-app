package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.aiscope.gdd_app.CoroutineTestRule
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.main.SelectDiseasePresenter
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class SelectDiseasePresenterTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private val repository: SampleRepository = mock()
    private val subject = SelectDiseasePresenter(mock(), mock(), repository)

    @Test
    fun `should store a disease with a name in the repository`() =
        coroutinesTestRule.runBlockingTest {
            whenever(repository.create(DISEASE_NAME)).thenReturn(
                Sample(
                    "id",
                    "hospital",
                    "a microscopist",
                    DISEASE_NAME
                )
            )

            subject.saveDisease(DISEASE_NAME)

            argumentCaptor<Sample>().apply {
                verify(repository).store(capture())
                assert(firstValue.disease == DISEASE_NAME)
            }
        }

    companion object {
        private const val DISEASE_NAME = "testDisease"
    }
}
