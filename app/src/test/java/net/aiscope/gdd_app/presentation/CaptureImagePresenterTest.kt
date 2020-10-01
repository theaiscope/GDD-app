package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import net.aiscope.gdd_app.CoroutineTestRule
import net.aiscope.gdd_app.model.Images
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.capture.CaptureImagePresenter
import net.aiscope.gdd_app.ui.capture.CaptureImageView
import org.junit.Rule
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
class CaptureImagePresenterTest {

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    @Test
    fun `capture image should store the image`() = coroutinesTestRule.runBlockingTest {
        val view: CaptureImageView = mock()
        val repository: SampleRepository = mock()
        val sample = Sample("an id", "a facility", "a microscopist", "a disease")
        val file = File.createTempFile("temp", ".jpg")

        whenever(repository.current()).thenReturn(sample)

        whenever(view.takePhoto(any(), any())).doAnswer {
            val cb = it.getArgument(1) as suspend (File?) -> Unit
            coroutinesTestRule.runBlockingTest { cb(file) }
            Unit
        }

        val presenter = CaptureImagePresenter(view, repository)
        presenter.handleCaptureImageButton("any")

        verify(repository).store(sample.copy(images = Images().newCapture(file)))
    }

}
