package net.aiscope.gdd_app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class NewHealthFacilityActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_health_facility)

        val saveButton = findViewById<Button>(R.id.button_save_new_health_facility)
        val cancelButton = findViewById<Button>(R.id.button_cancel_new_health_facility)
        val healthFacilityText = findViewById<TextInputEditText>(R.id.text_health_facility_name_field)

        saveButton.setOnClickListener {
            val message =
            if (!healthFacilityText.text.isNullOrEmpty()) {
                R.string.confirmation_message_health_facility_saved
            } else {
                R.string.error_message_field_empty
            }
            val toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
            toast.show()
        }

        cancelButton.setOnClickListener {
            this.finish()
        }

    }
}
