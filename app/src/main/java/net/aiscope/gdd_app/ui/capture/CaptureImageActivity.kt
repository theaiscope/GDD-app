package net.aiscope.gdd_app.ui.capture

import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import io.fotoapparat.Fotoapparat
import kotlinx.android.synthetic.main.activity_capture_image.*
import kotlinx.android.synthetic.main.toolbar.*
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.extensions.writeToFile
import net.aiscope.gdd_app.ui.main.MainActivity
import net.aiscope.gdd_app.ui.mask.MaskActivity
import java.io.File
import javax.inject.Inject

class CaptureImageActivity : AppCompatActivity(), CaptureImageView {

    companion object {
        const val EXTRA_IMAGE_NAME = "net.aiscope.gdd_app.ui.capture.CaptureImageActivity.EXTRA_IMAGE_NAME"
        const val EXTRA_MASK_NAME = "net.aiscope.gdd_app.ui.capture.CaptureImageActivity.EXTRA_MASK_NAME"
    }

    @Inject
    lateinit var presenter: CaptureImagePresenter

    private lateinit var fotoapparat: Fotoapparat

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_image)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            val alertDialog: AlertDialog? = this.let {
                val builder = AlertDialog.Builder(it, R.style.MyAlertDialogStyle)
                builder.apply {
                    setPositiveButton(R.string.ok
                    ) { _, _ ->
                        // User clicked OK button
                        val intent = Intent(context, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                        startActivity(intent)
                    }
                    setNegativeButton(R.string.cancel
                    ) { dialog, _ ->
                        // User cancelled the dialog
                        dialog.dismiss()
                    }
                }
                // Set other dialog properties
                builder.setMessage("Are you sure you want to exit? Your current data will be lost")
                builder.setTitle("Warning!")
                // Create the AlertDialog
                builder.create()
            }
            alertDialog?.show()
        }

        fotoapparat = Fotoapparat(
            context = this,
            view = camera_view,
            cameraErrorCallback = { presenter.onCaptureError(it) }
        )

        capture_image_button.setOnClickListener {
            presenter.handleCaptureImageButton(extractImageNameExtra(), extractMaskNameExtra())
        }
    }

    private fun extractImageNameExtra() = intent.getStringExtra(EXTRA_IMAGE_NAME)

    private fun extractMaskNameExtra() = intent.getStringExtra(EXTRA_MASK_NAME)

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            presenter.handleCaptureImageButton(extractImageNameExtra(), extractMaskNameExtra())
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        fotoapparat.start()
        super.onStart()
    }

    override fun onStop() {
        fotoapparat.stop()
        super.onStop()
    }

    override fun takePhoto(imageName: String, onPhotoReceived: (File?) -> Unit) {
        val result = fotoapparat.takePicture()
        val dest = File(this.filesDir, "${imageName}.jpg")
        result.toBitmap().whenAvailable {
            it?.let {
                val degrees = (-it.rotationDegrees) % 360
                val bmp = it.bitmap.rotate(degrees.toFloat())
                bmp.writeToFile(dest)

                onPhotoReceived(dest)
            } ?: notifyImageCouldNotBeTaken()
        }
    }


    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    override fun notifyImageCouldNotBeTaken() {
        Toast.makeText(this, getString(R.string.image_could_not_be_taken), Toast.LENGTH_SHORT).show()
    }


    override fun goToMask(imagePath: String, nextMaskName: String) {
        val intent = Intent(this, MaskActivity::class.java)
        intent.putExtra(MaskActivity.EXTRA_IMAGE_NAME, imagePath)
        intent.putExtra(MaskActivity.EXTRA_MASK_NAME, nextMaskName)

        startActivity(intent)
    }

}


