package net.aiscope.gdd_app.ui.newHealthFacility

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.application.GddApplication
import javax.inject.Inject


class NewHealthFacilityActivity : AppCompatActivity() {
    @Inject lateinit var presenter: NewHealthFacilityPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_health_facility)
        (application as GddApplication).gddComponent.inject(this)
        presenter.setView(this)

        val saveButton = findViewById<Button>(R.id.button_save_new_health_facility)
        val cancelButton = findViewById<Button>(R.id.button_cancel_new_health_facility)
        val healthFacilityText = findViewById<EditText>(R.id.text_health_facility_name_field)

        saveButton.setOnClickListener {
            presenter.handleSaveButtonClick(healthFacilityText)
        }

        cancelButton.setOnClickListener {
            presenter.destroyActivity()
        }

    }



}
