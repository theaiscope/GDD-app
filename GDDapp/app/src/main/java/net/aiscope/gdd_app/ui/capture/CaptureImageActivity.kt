package net.aiscope.gdd_app.ui.capture

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import io.fotoapparat.Fotoapparat
import io.fotoapparat.result.BitmapPhoto
import kotlinx.android.synthetic.main.activity_capture_image.*
import net.aiscope.gdd_app.R


class CaptureImageActivity : AppCompatActivity(), CaptureImageView {

    private val presenter: CaptureImagePresenter = CaptureImagePresenter(this)

    lateinit var fotoapparat: Fotoapparat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_image)

        fotoapparat = Fotoapparat(
            context = this,
            view = camera_view
        )

        fotoapparat.start()

        capture_image_button.setOnClickListener {
            presenter.handleCaptureImageButton()
        }
    }

    override fun takePhoto(cb: (BitmapPhoto?) -> Unit) {
        fotoapparat
            .takePicture()
            .toBitmap()
            .whenAvailable {cb}
    }

    override fun setPreviewImage(bm: Bitmap?) {
        findViewById<ImageView>(R.id.capture_image_preview).setImageBitmap(bm)
    }

}


