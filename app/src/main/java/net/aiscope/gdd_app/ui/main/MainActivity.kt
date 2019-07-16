package net.aiscope.gdd_app.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.mask.MaskActivity
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityActivity
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseaseActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askCameraPermission()

        val buttonNewHealthFacility = findViewById<Button>(R.id.button_go_to_new_health_facility)
        val buttonSelectDisease = findViewById<Button>(R.id.button_go_to_select_disease)

        buttonNewHealthFacility.setOnClickListener {
            startActivity(Intent(this, NewHealthFacilityActivity::class.java))
        }

        buttonSelectDisease.setOnClickListener {
            startActivity(Intent(this, SelectDiseaseActivity::class.java))
        }
    }

    private fun askCameraPermission() {
        val listener = object : PermissionListener {
            override fun onPermissionGranted(response: PermissionGrantedResponse?) {
            }

            override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
            }

            override fun onPermissionDenied(response: PermissionDeniedResponse?) {
            }

        }
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(listener)
            .onSameThread()
            .check();
    }

}
