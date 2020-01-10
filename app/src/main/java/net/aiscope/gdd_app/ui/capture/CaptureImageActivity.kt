package net.aiscope.gdd_app.ui.capture

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.ScaleGestureDetector
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import dagger.android.AndroidInjection
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.Zoom
import kotlinx.android.synthetic.main.activity_capture_image.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.extensions.rotate
import net.aiscope.gdd_app.extensions.writeToFileAsync
import net.aiscope.gdd_app.ui.attachCaptureFlowToolbar
import net.aiscope.gdd_app.ui.mask.MaskActivity
import java.io.File
import javax.inject.Inject


const val THREE_SIXTY_DEGREES = 360

class CaptureImageActivity : AppCompatActivity(), CaptureImageView {

    companion object {
        const val EXTRA_IMAGE_NAME = "net.aiscope.gdd_app.ui.capture.CaptureImageActivity.EXTRA_IMAGE_NAME"
        const val EXTRA_MASK_NAME = "net.aiscope.gdd_app.ui.capture.CaptureImageActivity.EXTRA_MASK_NAME"
    }

    @Inject
    lateinit var presenter: CaptureImagePresenter

    private val parentJob = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + parentJob)

    private lateinit var fotoapparat: Fotoapparat

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
        super.onStart()
        fotoapparat.start()

        fotoapparat.getCapabilities().whenAvailable { capabilities ->
            when (val zoom = capabilities?.zoom) {
                is Zoom.VariableZoom -> {
                    val maxZoomRatio = zoom.zoomRatios.last() / 100f
                    setUpZoomSeekBar(maxZoomRatio)
                    setUpPinchToZoom(maxZoomRatio)
                }
                else -> {
                    hideZoomLevelSeekBar()
                }
            }
        }
    }

    override fun onStop() {
        fotoapparat.stop()
        super.onStop()
    }

    override fun onDestroy() {
        parentJob.cancel()
        super.onDestroy()
    }

    override fun takePhoto(imageName: String, onPhotoReceived: (File?) -> Unit) {
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
        Toast.makeText(this, getString(R.string.image_could_not_be_taken), Toast.LENGTH_SHORT).show()
    }

    override fun goToMask(imagePath: String, nextMaskName: String) {
        val intent = Intent(this, MaskActivity::class.java)
        intent.putExtra(MaskActivity.EXTRA_IMAGE_NAME, imagePath)
        intent.putExtra(MaskActivity.EXTRA_MASK_NAME, nextMaskName)

        startActivity(intent)
    }


    private var zoomScaleFactor = 1f

    private fun setUpZoomSeekBar(maxZoomRatio: Float) {
        camera_zoom_level.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val zoomLevel = progress / 100f
                zoomScaleFactor = (maxZoomRatio - 1) * zoomLevel + 1
                fotoapparat.setZoom(zoomLevel)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                showZoomLevelSeekBar()
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                hideZoomLevelSeekBar()
            }
        })
    }

    private fun setUpPinchToZoom(maxZoomRatio: Float) {
        val scaleGestureDetector =
            ScaleGestureDetector(this, object : ScaleGestureDetector.OnScaleGestureListener {

                override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
                    showZoomLevelSeekBar()
                    return true
                }

                override fun onScaleEnd(detector: ScaleGestureDetector) {
                    hideZoomLevelSeekBar()
                }

                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    zoomScaleFactor = (zoomScaleFactor * detector.scaleFactor).coerceAtLeast(1f)
                        .coerceAtMost(maxZoomRatio)

                    val zoomLevel = (zoomScaleFactor - 1) / (maxZoomRatio - 1)
                    camera_zoom_level.progress = (zoomLevel * 100f).toInt()
                    fotoapparat.setZoom(zoomLevel)

                    return true
                }
            })

        camera_view.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
        }
    }

    private val handler: Handler = Handler()
    private val hideZoomLevelSeekBarRunnable = Runnable { camera_zoom_level.isVisible = false }

    private fun hideZoomLevelSeekBar() {
        handler.removeCallbacks(hideZoomLevelSeekBarRunnable)
        handler.postDelayed(hideZoomLevelSeekBarRunnable, 2000)

    }

    private fun showZoomLevelSeekBar() {
        handler.removeCallbacks(hideZoomLevelSeekBarRunnable)
        camera_zoom_level.isVisible = true
    }
}
