package net.aiscope.gdd_app.ui.capture

import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CaptureImagePresenterTest {

    @Mock
    lateinit var view: CaptureImageView

    @Test
    fun `handleCaptureImageButton calls the capture image process in the view`() {
        val presenter = CaptureImagePresenter(view)

        presenter.handleCaptureImageButton()

        verify(view, times(1)).takePhoto()
    }

    @Test
    fun `setImage stores the image bytes in memory`() {
        val presenter = CaptureImagePresenter(view)
        val bytes :ByteArray = "testBytes".toByteArray()

        presenter.storeImageData(bytes)

        assert(presenter.byteImageArray.contentEquals(bytes))
    }
}