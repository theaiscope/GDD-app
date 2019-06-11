package net.aiscope.gdd_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.aiscope.gdd_app.repository.HospitalRepository
import net.aiscope.gdd_app.repository.SharedPreferencesRepository


class NewHealthFacilityActivity : AppCompatActivity() {
    val healthFacilityRepository: HospitalRepository = SharedPreferencesRepository(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_health_facility)

        val saveButton = findViewById<Button>(R.id.button_save_new_health_facility)
        val cancelButton = findViewById<Button>(R.id.button_cancel_new_health_facility)
        val healthFacilityText = findViewById<EditText>(R.id.text_health_facility_name_field)

        saveButton.setOnClickListener {
            handleSaveButtonClick(healthFacilityText)
        }

        cancelButton.setOnClickListener {
            this.finish()
        }

    }

    private fun handleSaveButtonClick(healthFacilityText: EditText) {
        val message =
            if (healthFacilityText.text.isNotEmpty()) {
                saveHealthFacility(healthFacilityText.text.toString())
                R.string.confirmation_message_health_facility_saved
            } else {
                R.string.error_message_field_empty
            }
        val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        toast.show()
    }

    fun saveHealthFacility(hospitalName: String) {
        healthFacilityRepository.store(hospitalName)
    }

}
