package net.aiscope.gdd_app.ui.capture

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
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
            view = camera_view,
            cameraErrorCallback = { presenter.onCaptureError(it) }
        )

        capture_image_button.setOnClickListener { presenter.handleCaptureImageButton() }
    }

    override fun onStart() {
        fotoapparat.start()
        super.onStart()
    }

    override fun onStop() {
        fotoapparat.stop()
        super.onStop()
    }

    override fun takePhoto(onPhotoReceived: (BitmapPhoto?) -> Unit) {
        fotoapparat
            .takePicture()
            .toBitmap()
            .whenAvailable { onPhotoReceived(it) }
    }

    override fun setPreviewImage(photo: BitmapPhoto) {
        val imageView = findViewById<ImageView>(R.id.capture_image_preview)
        imageView.rotation = (-photo.rotationDegrees).toFloat()
        imageView.setImageBitmap(photo.bitmap)
    }

    override fun notifyImageCouldNotBeTaken() {
        Toast.makeText(this, getString(R.string.image_could_not_be_taken), Toast.LENGTH_SHORT).show()
    }

}


