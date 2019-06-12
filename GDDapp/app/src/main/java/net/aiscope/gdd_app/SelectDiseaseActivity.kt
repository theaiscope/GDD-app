package net.aiscope.gdd_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SelectDiseaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_disease)

        val captureImageButton = findViewById<Button>(R.id.button_capture_image_select_disease)

        captureImageButton.setOnClickListener {
            startActivity(Intent(this, CaptureImageActivity::class.java))
        }
    }
}
