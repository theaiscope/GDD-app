package net.aiscope.gdd_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button_go_to_new_health_facility)
        val intent = Intent(this, NewHealthFacilityActivity::class.java)

        button.setOnClickListener {
            startActivity(intent)
        }
    }
}
