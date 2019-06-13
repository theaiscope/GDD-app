package net.aiscope.gdd_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.aiscope.gdd_app.model.Disease
import net.aiscope.gdd_app.repository.Repository
import net.aiscope.gdd_app.repository.SharedPreferencesRepository

class SelectDiseaseActivity : AppCompatActivity() {
    private val repository: Repository = SharedPreferencesRepository(this)

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
        val diseaseName = selectedDisease.selectedItem.toString()
        val message =
            if (diseaseName == "") {
                R.string.error_message_field_empty
            } else {
                saveDisease(Disease(diseaseName))
                startActivity(Intent(this, CaptureImageActivity::class.java))
                R.string.confirmation_message_saved
            }
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    private fun saveDisease(disease: Disease) {
        repository.store(disease)
    }
}
