package net.aiscope.gdd_app.ui.capture

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import io.fotoapparat.Fotoapparat
import kotlinx.android.synthetic.main.activity_capture_image.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.extensions.rotate
import net.aiscope.gdd_app.extensions.writeToFileAsync
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.mask.MaskActivity
import java.io.File
import javax.inject.Inject


const val THREE_SIXTY_DEGREES = 360

class CaptureImageActivity : AppCompatActivity(), CaptureImageView, CaptureFlow {

    companion object {
        const val EXTRA_IMAGE_NAME = "net.aiscope.gdd_app.ui.capture.CaptureImageActivity.EXTRA_IMAGE_NAME"
    }

    @Inject
    lateinit var presenter: CaptureImagePresenter

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    private lateinit var fotoapparat: Fotoapparat
    private lateinit var zoomController: ZoomController

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_image)
        setSupportActionBar(toolbar)
        attachCaptureFlowToolbar(toolbar)

        fotoapparat = Fotoapparat(
            context = this,
            view = camera_view,
            cameraErrorCallback = { presenter.onCaptureError(it) }
        )
        zoomController = ZoomController(fotoapparat, camera_zoom_level, camera_view)

        capture_image_button.setOnClickListener {
            presenter.handleCaptureImageButton(extractImageNameExtra())
        }
    }

    private fun extractImageNameExtra() = checkNotNull(intent.getStringExtra(EXTRA_IMAGE_NAME))

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            presenter.handleCaptureImageButton(extractImageNameExtra())
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onStart() {
        super.onStart()
        fotoapparat.start()
        zoomController.init()
    }

    override fun onStop() {
        fotoapparat.stop()
        super.onStop()
    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    override fun takePhoto(imageName: String, onPhotoReceived: suspend (File?) -> Unit) {
        val result = fotoapparat.takePicture()
        val dest = File(this.filesDir, "${imageName}.jpg")
        result.toBitmap().whenAvailable {
            it?.let {
                val degrees = (-it.rotationDegrees) % THREE_SIXTY_DEGREES
                coroutineScope.launch {
                    val bmp = it.bitmap.rotate(degrees.toFloat())
                    bmp.writeToFileAsync(dest)
                    onPhotoReceived(dest)
                }
            } ?: notifyImageCouldNotBeTaken()
        }
    }

    override fun notifyImageCouldNotBeTaken() {
        val message = getString(R.string.image_could_not_be_taken)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun goToMask(diseaseName: String, imagePath: String, nextMaskName: String) {
        val intent = Intent(this, MaskActivity::class.java)
        intent.putExtra(MaskActivity.EXTRA_DISEASE_NAME, diseaseName)
        intent.putExtra(MaskActivity.EXTRA_IMAGE_NAME, imagePath)
        intent.putExtra(MaskActivity.EXTRA_MASK_NAME, nextMaskName)

        startActivity(intent)
    }
}
