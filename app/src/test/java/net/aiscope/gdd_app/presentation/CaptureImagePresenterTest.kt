package net.aiscope.gdd_app.presentation

import com.nhaarman.mockito_kotlin.*
import net.aiscope.gdd_app.model.Sample
import net.aiscope.gdd_app.repository.SampleRepository
import net.aiscope.gdd_app.ui.capture.CaptureImagePresenter
import net.aiscope.gdd_app.ui.capture.CaptureImageView
import org.junit.Test
import java.io.File

class CaptureImagePresenterTest {

    @Test
    fun `capture image should store the image`() {
        val view: CaptureImageView = mock()
        val repository: SampleRepository = mock()
        val sample = Sample("an id", "a facility")
        val file = File.createTempFile("temp", ".png")

        whenever(repository.current()).thenReturn(sample)

        whenever(view.takePhoto(any(), any())).doAnswer {
            val cb = it.getArgument(1) as (File?) -> Unit
            cb(file)
            Unit
        }

        val presenter = CaptureImagePresenter(view, repository)
        presenter.handleCaptureImageButton("any", "any")

        verify(repository).store(sample.copy(images = linkedSetOf(file)))
    }

}
