package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.aiscope.gdd_app.CoroutineTestRule
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.network.FirebaseAuthenticator
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

    @Mock
    lateinit var firebaseAuth: FirebaseAuthenticator

    @InjectMocks
    lateinit var subject: SelectDiseasePresenter

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
