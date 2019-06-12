package net.aiscope.gdd_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SelectDiseaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_disease)

        val captureImageButton = findViewById<Button>(R.id.button_capture_image_select_disease)
        val diseasesSpinner = findViewById<Spinner>(R.id.spinner_diseases)

        captureImageButton.setOnClickListener {
            handleSaveButtonClick(diseasesSpinner)
        }
    }

    private fun handleSaveButtonClick(selectedDisease: Spinner) {
        val message =
            if (selectedDisease.selectedItem.toString() == "") {
                R.string.error_message_field_empty
            } else {
                startActivity(Intent(this, CaptureImageActivity::class.java))
                R.string.confirmation_message_saved
            }
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()
    }
}
