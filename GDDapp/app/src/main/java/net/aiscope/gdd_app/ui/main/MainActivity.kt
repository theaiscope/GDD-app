package net.aiscope.gdd_app.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import net.aiscope.gdd_app.ui.newHealthFacility.NewHealthFacilityActivity
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.selectDisease.SelectDiseaseActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonNewHealthFacility = findViewById<Button>(R.id.button_go_to_new_health_facility)
        val buttonSelectDisease = findViewById<Button>(R.id.button_go_to_select_disease)

        buttonNewHealthFacility.setOnClickListener {
            startActivity(Intent(this, NewHealthFacilityActivity::class.java))
        }

        buttonSelectDisease.setOnClickListener {
            startActivity(Intent(this, SelectDiseaseActivity::class.java))
        }
    }
}
