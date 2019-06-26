package net.aiscope.gdd_app.presentation

import net.aiscope.gdd_app.ui.capture.CaptureImageView
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class CaptureImagePresenterTest {

    @Mock
    lateinit var view: CaptureImageView

    @Test
    fun addition_isCorrect() {
        Assert.assertEquals(4, 2 + 2)
    }


//    @Test
//    fun `capture image should store the image`() {
//        whenever(view.takePhoto(any())).doAnswer {
//            val cb = it.getArgument(0) as (BitmapPhoto?) -> Unit
//            val bitmap = BitmapFactory.decodeByteArray("testBitMap".toByteArray(), 0, 50)
//            val testImage = BitmapPhoto(bitmap, 0)
//            cb(testImage)
//            Unit
//        }
//
//        val presenter = CaptureImagePresenter(view)
//
//        presenter.handleCaptureImageButton()
//
//    }

}
