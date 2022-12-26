package net.aiscope.gdd_app.ui.capture

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import dagger.android.AndroidInjection
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.selector.highestResolution
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.databinding.ActivityCaptureImageBinding
import net.aiscope.gdd_app.extensions.rotate
import net.aiscope.gdd_app.extensions.writeToFileAsync
import net.aiscope.gdd_app.ui.CaptureFlow
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.mask.MaskActivity
import java.io.File
import javax.inject.Inject


const val THREE_SIXTY_DEGREES = 360

@Suppress("TooManyFunctions")
class CaptureImageActivity : AppCompatActivity(), CaptureImageView, CaptureFlow {

    companion object {
        const val EXTRA_IMAGE_NAME = "net.aiscope.gdd_app.ui.capture.CaptureImageActivity.EXTRA_IMAGE_NAME"
        const val CAPTURE_IMAGE_FROM ="CAPTURE_IMAGE_FROM"
    }

    @Inject
    lateinit var presenter: CaptureImagePresenter

    private lateinit var fotoapparat: Fotoapparat
    private lateinit var zoomController: ZoomController

    private lateinit var binding: ActivityCaptureImageBinding
    private var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)

        binding = ActivityCaptureImageBinding.inflate(layoutInflater)
        with(binding) {
            setContentView(root)
            setSupportActionBar(toolbarLayout.toolbar)
            attachCaptureFlowToolbar(toolbarLayout.toolbar)

            fotoapparat = Fotoapparat(
                context = this@CaptureImageActivity,
                view = cameraView,
                cameraConfiguration =
                CameraConfiguration.builder().photoResolution(highestResolution()).build(),
                cameraErrorCallback = { presenter.onCaptureError(it) }
            )

            toast  = Toast.makeText(
                this@CaptureImageActivity,
                R.string.you_cannot_take_a_picture_while_zooming,
                Toast.LENGTH_SHORT)

            val onZoomChanged = ZoomController.OnZoomChangedListener { isZoomed ->
                if (isZoomed) {
                    captureImageButton.isEnabled = false
                    toast?.show()
                } else {
                    captureImageButton.isEnabled = true
                    toast?.cancel()
                }
            }

            zoomController = ZoomController(fotoapparat, cameraZoomLevel, cameraView, onZoomChanged)

            captureImageButton.setOnClickListener {
                presenter.handleCaptureImageButton(extractImageNameExtra(), extractFrom())
            }
        }
    }

    private fun extractImageNameExtra() = checkNotNull(intent.getStringExtra(EXTRA_IMAGE_NAME))

    private fun extractFrom() = checkNotNull(intent.getStringExtra(CAPTURE_IMAGE_FROM))

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            presenter.handleCaptureImageButton(extractImageNameExtra(), extractFrom())
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

    override fun takePhoto(imageName: String, onPhotoReceived: suspend (File?) -> Unit) {
        binding.captureImageLoadingModal.isVisible = true
        val result = fotoapparat.takePicture()
        val dest = File(this.filesDir, "${imageName}.jpg")
        result.toBitmap().whenAvailable {
            it?.let {
                val degrees = (-it.rotationDegrees) % THREE_SIXTY_DEGREES
                lifecycleScope.launch {
                    val bmp = it.bitmap.rotate(degrees.toFloat())
                    bmp.writeToFileAsync(dest, Bitmap.CompressFormat.JPEG)
                    onPhotoReceived(dest)
                }
            } ?: lifecycleScope.launch { onPhotoReceived(null) }
        }
    }

    override fun notifyImageCouldNotBeTaken() {
        val message = getString(R.string.image_could_not_be_taken)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        binding.captureImageLoadingModal.isGone = true
    }

    override fun goToMask(diseaseName: String, imagePath: String, nextMaskName: String, viewClassFrom: String) {
        val intent = Intent(this, MaskActivity::class.java)
        intent.putExtra(MaskActivity.EXTRA_DISEASE_NAME, diseaseName)
        intent.putExtra(MaskActivity.EXTRA_IMAGE_NAME, imagePath)
        intent.putExtra(MaskActivity.EXTRA_MASK_NAME, nextMaskName)
        intent.putExtra(MaskActivity.EXTRA_MASK_FROM, viewClassFrom)

        startActivity(intent)
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        binding.captureImageLoadingModal.isGone = true
    }

    override fun onDestroy() {
        toast?.cancel()
        toast = null
        super.onDestroy()
    }
}
