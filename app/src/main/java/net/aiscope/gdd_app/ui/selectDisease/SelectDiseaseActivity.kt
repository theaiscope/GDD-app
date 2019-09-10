package net.aiscope.gdd_app.ui.selectDisease

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dagger.android.AndroidInjection
import net.aiscope.gdd_app.R
import net.aiscope.gdd_app.ui.capture.CaptureImageActivity
import javax.inject.Inject

class SelectDiseaseActivity : AppCompatActivity() , SelectDiseaseView{
    @Inject lateinit var presenter: SelectDiseasePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_disease)

        val captureImageButton = findViewById<Button>(R.id.button_capture_image_select_disease)
        val diseasesSpinner = findViewById<Spinner>(R.id.spinner_diseases)
        val cancelButton = findViewById<Button>(R.id.button_back_select_disease)
        diseasesSpinner.setSelection(0, true)

        captureImageButton.setOnClickListener {
            presenter.saveDisease(diseasesSpinner.selectedItem.toString())
        }

        cancelButton.setOnClickListener {
            this.finish()
        }
    }

    override fun captureImage(nextImageName: String, nextMaskName: String) {
        val intent = Intent(this, CaptureImageActivity::class.java)
        intent.putExtra(CaptureImageActivity.EXTRA_IMAGE_NAME, nextImageName)
        intent.putExtra(CaptureImageActivity.EXTRA_MASK_NAME, nextMaskName)
        this.startActivity(intent)
    }

    override fun showSuccessToast() {
        Toast.makeText(this, R.string.confirmation_message_saved, Toast.LENGTH_SHORT).show()
    }

    override fun showFailureToast() {
        Toast.makeText(this, R.string.error_message_field_empty, Toast.LENGTH_SHORT).show()
    }
}
