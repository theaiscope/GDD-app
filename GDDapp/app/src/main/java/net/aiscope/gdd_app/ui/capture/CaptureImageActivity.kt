package net.aiscope.gdd_app.ui.capture

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.camerakit.CameraKit
import com.camerakit.CameraKitView
import kotlinx.android.synthetic.main.activity_capture_image.*
import net.aiscope.gdd_app.R


class CaptureImageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_image)
        cameraKitView = findViewById(R.id.camera)
        cameraKitView.focus = CameraKit.FOCUS_OFF
        cameraKitView.flash = CameraKit.FLASH_OFF
        cameraKitView.facing = CameraKit.FACING_BACK


        capture_image_button.setOnClickListener {
            Log.e("Taking picture", "trying to take a picture")

            cameraKitView.captureImage { view, byteImage ->
                Log.e("Taking picture", "callback called")
                val imgViewer = findViewById<ImageView>(R.id.capture_image_preview)
                val bm = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.size)

                imgViewer.setImageBitmap(bm)
            }
        }

        cameraKitView.errorListener = CameraKitView.ErrorListener { cameraKitView, e ->
            Log.e(localClassName,"error in camera", e)
        }
    }

    private lateinit var cameraKitView: CameraKitView

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

}


