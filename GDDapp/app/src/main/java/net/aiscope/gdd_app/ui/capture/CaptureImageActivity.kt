package net.aiscope.gdd_app.ui.capture

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.camerakit.CameraKit
import com.camerakit.CameraKitView
import kotlinx.android.synthetic.main.activity_capture_image.*
import net.aiscope.gdd_app.R


class CaptureImageActivity : AppCompatActivity(), CaptureImageView {

    private val presenter: CaptureImagePresenter = CaptureImagePresenter(this)
    lateinit var cameraKitView: CameraKitView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_image)

        cameraKitView = setupCamera()

        capture_image_button.setOnClickListener { presenter.handleCaptureImageButton() }

        cameraKitView.errorListener = CameraKitView.ErrorListener { _: View, e: Exception -> onCameraError(e.message) }
    }

    override fun takePhoto() {
        cameraKitView.captureImage { _, byteImage ->
            presenter.storeImageData(byteImage)
            presenter.setPreviewImage(byteImage)
        }
    }

    override var onCameraError: (String?) -> Unit = {}

    override fun setPreviewImage(bm: Bitmap?) {
        findViewById<ImageView>(R.id.capture_image_preview).setImageBitmap(bm)
    }

    override fun onStart() {
        super.onStart()
        cameraKitView.onStart()
    }

    override fun onResume() {
        super.onResume()
        cameraKitView.onResume()
    }

    override fun onPause() {
        cameraKitView.onPause()
        super.onPause()
    }

    override fun onStop() {
        cameraKitView.onStop()
        super.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraKitView.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setupCamera(): CameraKitView {
        val cam: CameraKitView = findViewById(R.id.camera)
        cam.focus = CameraKit.FOCUS_OFF
        cam.flash = CameraKit.FLASH_OFF
        cam.facing = CameraKit.FACING_BACK
        return cam
    }

}


