package net.aiscope.gdd_app.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_main.*
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseaseActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askCameraPermission()

        button_go_to_select_disease.setOnClickListener {
            startActivity(Intent(this, SelectDiseaseActivity::class.java))
        }
    }

    private fun askCameraPermission() {
        val listener = object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                // do nothing
            }

            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
                // do nothing
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                // do nothing
            }
        }
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(listener)
            .onSameThread()
            .check()
    }
}
