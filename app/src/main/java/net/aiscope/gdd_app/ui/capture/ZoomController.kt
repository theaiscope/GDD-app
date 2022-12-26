package net.aiscope.gdd_app.ui.capture

import android.os.Handler
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import io.fotoapparat.Fotoapparat
import io.fotoapparat.parameter.Zoom

class ZoomController(
    private val fotoapparat: Fotoapparat,
    private val cameraZoomLevel: SeekBar,
    private val cameraView: View,
    private val onZoomChangedListener : OnZoomChangedListener?
) {
    companion object {
        const val PERCENTAGE_RATIO = 100f
        const val SEEK_BAR_VISIBILITY_DELAY_IN_MILLIS = 2000L
    }

    fun init() {
        fotoapparat.getCapabilities().whenAvailable { capabilities ->
            when (val zoom = capabilities?.zoom) {
                is Zoom.VariableZoom -> {
                    val maxZoomRatio =
                        zoom.zoomRatios.last() / PERCENTAGE_RATIO
                    setUpZoomSeekBar(maxZoomRatio)
                    setUpPinchToZoom(maxZoomRatio)
                }
                else -> {
                    hideZoomLevelSeekBar()
                }
            }
        }
    }

    private var zoomScaleFactor = 1f

    private fun setUpZoomSeekBar(maxZoomRatio: Float) {
        cameraZoomLevel.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val zoomLevel = progress / PERCENTAGE_RATIO
                zoomScaleFactor = (maxZoomRatio - 1) * zoomLevel + 1
                fotoapparat.setZoom(zoomLevel)

                onZoomChanged(zoomScaleFactor)
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
            ScaleGestureDetector(cameraView.context, object : ScaleGestureDetector.OnScaleGestureListener {

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
                    cameraZoomLevel.progress = (zoomLevel * PERCENTAGE_RATIO).toInt()
                    fotoapparat.setZoom(zoomLevel)

                    onZoomChanged(zoomScaleFactor)

                    return true
                }
            })

        cameraView.setOnTouchListener { _, event ->
            scaleGestureDetector.onTouchEvent(event)
        }
    }

    private val handler: Handler = Handler()
    private val hideZoomLevelSeekBarRunnable = Runnable { cameraZoomLevel.isVisible = false }

    private fun hideZoomLevelSeekBar() {
        handler.removeCallbacks(hideZoomLevelSeekBarRunnable)
        handler.postDelayed(hideZoomLevelSeekBarRunnable, SEEK_BAR_VISIBILITY_DELAY_IN_MILLIS)

    }

    private fun showZoomLevelSeekBar() {
        handler.removeCallbacks(hideZoomLevelSeekBarRunnable)
        cameraZoomLevel.isVisible = true
    }

    private fun onZoomChanged(zoom: Float) {
        // it is assumed that when the value provided here is equal to 1.0
        // then the image is not zoomed
        // and also that zoom cannot be less than 1.0
        onZoomChangedListener?.onZoomChanged(isZoomed = zoom > 1.0f)
    }

    fun interface OnZoomChangedListener {
        fun onZoomChanged(isZoomed: Boolean)
    }
}
