package net.aiscope.gdd_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class NewHealthFacilityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_health_facility)

        val saveButton = findViewById<Button>(R.id.button_save_new_health_facility)
        val healthFacilityText = findViewById<EditText>(R.id.text_health_facility_name_field)

        saveButton.setOnClickListener {
            if (healthFacilityText.text.isNotEmpty()) {
                Toast.makeText(this, R.string.confirmation_message_health_facility_saved, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.error_message_field_empty, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
