package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import net.aiscope.gdd_app.CoroutineTestRule
import net.aiscope.gdd_app.model.Captures
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.capture.CaptureImagePresenter
import net.aiscope.gdd_app.ui.capture.CaptureImageView
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File

@ExperimentalCoroutinesApi
class CaptureImagePresenterTest {
    companion object {
        private val sample = Sample("an id", "a facility", "a microscopist", "a disease")
        private val file = File.createTempFile("temp", ".jpg")
    }

    @get:Rule
    val coroutinesTestRule = CoroutineTestRule()

    private val view: CaptureImageView = mock()
    private val repository: SampleRepository = mock()
    private val presenter = CaptureImagePresenter(view, repository)

    @Before
    fun setUp(): Unit = runBlocking {
        whenever(repository.current()).thenReturn(sample)
    }

    @Test
    fun `capture image should store the image`() = coroutinesTestRule.runBlockingTest {
        whenever(view.takePhoto(any(), any())).doAnswer {
            val onPhotoReceived: suspend (File?) -> Unit = it.getArgument(1)
            runBlocking { onPhotoReceived(file) }
        }
        val presenter = CaptureImagePresenter(view, repository)
        presenter.handleCaptureImageButton("any", "any")
        verify(repository).store(sample.copy(captures = Captures().newCapture(file)))
    }

    @Test
    fun `calling capture image repeatedly takes only one picture`() {
        presenter.handleCaptureImageButton("any", "any")
        presenter.handleCaptureImageButton("any", "any")
        presenter.handleCaptureImageButton("any", "any")
        verify(view).takePhoto(any(), any())
    }

    @Test
    fun `can take picture again after image captured successfully`() {
        whenever(view.takePhoto(any(), any())).doAnswer {
            val onPhotoReceived: suspend (File?) -> Unit = it.getArgument(1)
            runBlocking { onPhotoReceived(file) }
        }
        presenter.handleCaptureImageButton("any", "any")
        presenter.handleCaptureImageButton("any", "any")
        verify(view, times(2)).takePhoto(any(), any())
    }

    @Test
    fun `can take picture again after image could't be captured`() {
        whenever(view.takePhoto(any(), any())).doAnswer {
            val onPhotoReceived: suspend (File?) -> Unit = it.getArgument(1)
            runBlocking { onPhotoReceived(null) }
        }
        presenter.handleCaptureImageButton("any", "any")
        presenter.handleCaptureImageButton("any", "any")
        verify(view, times(2)).takePhoto(any(), any())
    }
}
